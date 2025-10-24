package com.serviciudad.serviciudad.infrastructure.adapter.out.file;

import com.serviciudad.serviciudad.domain.model.FacturaEnergia;
import com.serviciudad.serviciudad.domain.port.out.FacturasEnergiaPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class AdaptadorArchivoEnergia implements FacturasEnergiaPort {

    private static final Logger log = LoggerFactory.getLogger(AdaptadorArchivoEnergia.class);

    private final String rutaConfigurada;
    private final ResourceLoader resourceLoader;

    public AdaptadorArchivoEnergia(@Value("${app.energia.archivo}") String rutaConfigurada,
                                   ResourceLoader resourceLoader) {
        this.rutaConfigurada = rutaConfigurada;
        this.resourceLoader = resourceLoader;
        log.info("AdaptadorArchivoEnergia inicializado. propiedad app.energia.archivo='{}'", rutaConfigurada);
    }

    @Override
    public List<FacturaEnergia> obtenerFacturas() {
        // Si la propiedad empieza con "classpath:" -> leer classpath
        if (rutaConfigurada != null && rutaConfigurada.startsWith("classpath:")) {
            String cp = rutaConfigurada.substring("classpath:".length());
            log.info("Leyendo archivo desde classpath: {}", cp);
            return leerDesdeClasspath(cp);
        }

        // Intentar ruta filesystem (relativa o absoluta)
        try {
            Path p = Paths.get(rutaConfigurada);
            log.info("Intentando leer archivo desde filesystem: {}", p.toAbsolutePath());
            if (Files.exists(p)) {
                try (Stream<String> lines = Files.lines(p)) {
                    return lines
                            .filter(line -> line != null && line.trim().length() > 0)
                            .map(this::parseLineaRobusta)
                            .collect(Collectors.toList());
                }
            } else {
                log.warn("Archivo no encontrado en filesystem: {}", p.toAbsolutePath());
            }
        } catch (Exception ex) {
            log.warn("Error al intentar leer desde filesystem la ruta '{}': {}", rutaConfigurada, ex.toString());
        }

        // Fallback: intentar classpath con la misma ruta (quitando prefijo ./ si existe)
        try {
            String cpCandidate = rutaConfigurada;
            if (cpCandidate.startsWith("./")) cpCandidate = cpCandidate.substring(2);
            log.info("Intentando leer archivo desde classpath fallback: {}", cpCandidate);
            return leerDesdeClasspath(cpCandidate);
        } catch (Exception ex) {
            log.error("No se pudo leer el archivo desde classpath ni filesystem. ruta='{}'", rutaConfigurada, ex);
            throw new RuntimeException("Error leyendo archivo de energia: " + rutaConfigurada, ex);
        }
    }

    private List<FacturaEnergia> leerDesdeClasspath(String cpPath) {
        Resource resource = resourceLoader.getResource("classpath:" + cpPath);
        if (!resource.exists()) {
            log.warn("Recurso classpath no existe: classpath:{}", cpPath);
            throw new RuntimeException("Archivo de energia no encontrado en classpath: " + cpPath);
        }

        try (InputStream in = resource.getInputStream();
             BufferedReader br = new BufferedReader(new InputStreamReader(in))) {

            return br.lines()
                    .filter(line -> line != null && line.trim().length() > 0)
                    .map(this::parseLineaRobusta)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Error leyendo recurso classpath: classpath:{}", cpPath, e);
            throw new RuntimeException("Error leyendo archivo de energia desde classpath: " + cpPath, e);
        }
    }

    /**
     * Parser robusto para líneas de ancho fijo donde:
     * - idCliente: posiciones 0..9  (10 chars)
     * - periodo:   posiciones 10..15 (6 chars)
     * - consumo:   posiciones 16..23 (8 chars) -> entero con ceros a la izquierda
     * - valor:     desde posición 24 hasta el final (puede tener punto decimal y longitud variable)
     */
    private FacturaEnergia parseLineaRobusta(String line) {
        String id = safeSubstring(line, 0, 10).trim();
        String periodo = safeSubstring(line, 10, 16).trim();
        String consumoStr = safeSubstring(line, 16, 24).trim();
        String valorStr = "";
        if (line != null && line.length() > 24) {
            valorStr = line.substring(24).trim(); // todo lo restante
        }

        int consumo = 0;
        if (!consumoStr.isEmpty()) {
            try { consumo = Integer.parseInt(consumoStr); }
            catch (NumberFormatException ex) {
                // intentar quitar ceros a la izquierda y parsear
                try { consumo = Integer.parseInt(consumoStr.replaceFirst("^0+(?!$)", "")); }
                catch (Exception e) { consumo = 0; }
            }
        }

        BigDecimal valor = BigDecimal.ZERO;
        if (!valorStr.isEmpty()) {
            try {
                // remover espacios y comas (si alguien usa coma como separador por error)
                String cleaned = valorStr.replace(",", "").trim();
                valor = new BigDecimal(cleaned);
            } catch (NumberFormatException ex) {
                log.warn("No se pudo parsear valor '{}' en linea: '{}', devolviendo 0", valorStr, line);
                valor = BigDecimal.ZERO;
            }
        }

        return new FacturaEnergia(id, periodo, consumo, valor);
    }

    private String safeSubstring(String s, int start, int end) {
        if (s == null) return "";
        if (s.length() <= start) return "";
        if (s.length() >= end) return s.substring(start, end);
        return s.substring(start);
    }
}

