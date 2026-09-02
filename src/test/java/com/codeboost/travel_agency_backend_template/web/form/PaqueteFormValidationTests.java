package com.codeboost.travel_agency_backend_template.web.form;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class PaqueteFormValidationTests {

    private static Validator validator;

    @BeforeAll
    static void configurarValidator() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void aceptaUnFormularioValido() {
        PaqueteForm form = formularioValido();

        assertThat(validator.validate(form)).isEmpty();
    }

    @Test
    void rechazaCamposObligatoriosVaciosYOrdenFueraDeRango() {
        PaqueteForm form = formularioValido();
        form.setNombre(" ");
        form.setDescripcion("corta");
        form.setDestino(null);
        form.setCategoria("");
        form.setOrden(0);
        form.setPrecio(new BigDecimal("-1.00"));

        assertThat(validator.validate(form))
                .extracting(error -> error.getPropertyPath().toString())
                .contains("nombre", "descripcion", "destino", "categoria", "orden", "precio");
    }

    @Test
    void conservaPaisesYCiudadesAlConvertirElFormulario() {
        PaqueteForm form = formularioValido();

        var paquete = form.aPaquete();
        PaqueteForm reconstruido = PaqueteForm.desde(paquete);

        assertThat(reconstruido.getDestino()).isEqualTo("México");
        assertThat(reconstruido.getCiudades()).isEqualTo("Cancún, Playa del Carmen, Tulum");
    }

    @Test
    void rechazaUnaListaDeCiudadesDemasiadoLarga() {
        PaqueteForm form = formularioValido();
        form.setCiudades("a".repeat(301));

        assertThat(validator.validate(form))
                .extracting(error -> error.getPropertyPath().toString())
                .contains("ciudades");
    }

    private PaqueteForm formularioValido() {
        PaqueteForm form = new PaqueteForm();
        form.setNombre("Caribe mexicano");
        form.setDescripcion("Paquete completo con hospedaje y traslados.");
        form.setDestino("México");
        form.setCiudades("Cancún, Playa del Carmen, Tulum");
        form.setCategoria("Playa");
        form.setBadges("Todo incluido,Viaje grupal");
        form.setPrecio(new BigDecimal("12500.00"));
        form.setOrden(1);
        return form;
    }
}
