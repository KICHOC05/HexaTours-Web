package com.codeboost.travel_agency_backend_template.controller;

import com.codeboost.travel_agency_backend_template.domain.model.Paquete;
import com.codeboost.travel_agency_backend_template.service.PaqueteService;
import com.codeboost.travel_agency_backend_template.service.UsuarioService;
import com.codeboost.travel_agency_backend_template.web.form.PaqueteForm;
import com.codeboost.travel_agency_backend_template.web.validation.ImageFileValidator;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/dashboard/paquetes")
@PreAuthorize("hasRole('ADMIN')")
public class AdminPaqueteController {

    private final PaqueteService paqueteService;
    private final UsuarioService usuarioService;

    public AdminPaqueteController(PaqueteService paqueteService,
                                  UsuarioService usuarioService) {
        this.paqueteService = paqueteService;
        this.usuarioService = usuarioService;
    }

    /* ── Lista ─────────────────────────────── */
    @GetMapping
    public String lista(
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {
        addUsuarioActual(userDetails, model);
        model.addAttribute("paquetes",   paqueteService.listarTodos());
        model.addAttribute("activePage", "paquetes");
        return "admin/paquetes/lista";
    }

    /* ── Formulario CREAR ───────────────────── */
    @GetMapping("/nuevo")
    public String nuevoForm(
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {
        addUsuarioActual(userDetails, model);
        model.addAttribute("paquete",    new PaqueteForm());
        model.addAttribute("activePage", "paquetes");
        model.addAttribute("accion",     "crear");
        return "admin/paquetes/form";
    }

    /* ── POST CREAR ─────────────────────────── */
    @PostMapping("/nuevo")
    public String crear(
            @Valid @ModelAttribute("paquete") PaqueteForm paquete,
            BindingResult bindingResult,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model,
            RedirectAttributes ra) {

        validarFormulario(paquete, bindingResult, true, null);
        if (bindingResult.hasErrors()) {
            return mostrarFormulario(paquete, "crear", userDetails, model);
        }

        try {
            paqueteService.guardar(paquete.aPaquete(), paquete.getImagen());
            ra.addFlashAttribute("success", "Paquete creado correctamente.");
            return "redirect:/dashboard/paquetes";
        } catch (IllegalArgumentException e) {
            bindingResult.reject("paquete.invalid", e.getMessage());
        } catch (Exception e) {
            bindingResult.reject("paquete.save", "No fue posible crear el paquete. Revisa los datos e intenta de nuevo.");
        }
        return mostrarFormulario(paquete, "crear", userDetails, model);
    }

    /* ── Formulario EDITAR ──────────────────── */
    @GetMapping("/{id}/editar")
    public String editarForm(@PathVariable Long id,
                             @AuthenticationPrincipal UserDetails userDetails,
                             Model model,
                             RedirectAttributes ra) {
        return paqueteService.buscarPorId(id).map(p -> {
            addUsuarioActual(userDetails, model);
            model.addAttribute("paquete",    PaqueteForm.desde(p));
            model.addAttribute("activePage", "paquetes");
            model.addAttribute("accion",     "editar");
            return "admin/paquetes/form";
        }).orElseGet(() -> {
            ra.addFlashAttribute("error", "Paquete no encontrado.");
            return "redirect:/dashboard/paquetes";
        });
    }

    /* ── POST EDITAR ────────────────────────── */
    @PostMapping("/{id}/editar")
    public String editar(
            @PathVariable Long id,
            @Valid @ModelAttribute("paquete") PaqueteForm paquete,
            BindingResult bindingResult,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model,
            RedirectAttributes ra) {

        Paquete existente = paqueteService.buscarPorId(id).orElse(null);
        if (existente == null) {
            ra.addFlashAttribute("error", "Paquete no encontrado.");
            return "redirect:/dashboard/paquetes";
        }

        paquete.setId(id);
        paquete.setImagenUrl(existente.getImagenUrl());
        validarFormulario(paquete, bindingResult, false, id);
        if (bindingResult.hasErrors()) {
            return mostrarFormulario(paquete, "editar", userDetails, model);
        }

        try {
            paqueteService.actualizar(id, paquete.aPaquete(), paquete.getImagen());
            ra.addFlashAttribute("success", "Paquete actualizado correctamente.");
            return "redirect:/dashboard/paquetes";
        } catch (IllegalArgumentException e) {
            bindingResult.reject("paquete.invalid", e.getMessage());
        } catch (Exception e) {
            bindingResult.reject("paquete.save", "No fue posible actualizar el paquete. Revisa los datos e intenta de nuevo.");
        }
        return mostrarFormulario(paquete, "editar", userDetails, model);
    }

    /* ── Toggle activo ─────────────────────── */
    @PostMapping("/{id}/toggle")
    public String toggle(@PathVariable Long id, RedirectAttributes ra) {
        paqueteService.toggleActivo(id);
        ra.addFlashAttribute("success", "Estado del paquete actualizado.");
        return "redirect:/dashboard/paquetes";
    }

    /* ── Eliminar ───────────────────────────── */
    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes ra) {
        try {
            paqueteService.eliminar(id);
            ra.addFlashAttribute("success", "Paquete eliminado correctamente.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/dashboard/paquetes";
    }

    private void addUsuarioActual(UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return;
        }
        usuarioService.buscarPorEmail(userDetails.getUsername())
                      .ifPresent(u -> model.addAttribute("usuario", u));
    }

    private String mostrarFormulario(PaqueteForm paquete,
                                     String accion,
                                     UserDetails userDetails,
                                     Model model) {
        addUsuarioActual(userDetails, model);
        model.addAttribute("paquete", paquete);
        model.addAttribute("activePage", "paquetes");
        model.addAttribute("accion", accion);
        return "admin/paquetes/form";
    }

    private void validarFormulario(PaqueteForm paquete,
                                   BindingResult bindingResult,
                                   boolean creando,
                                   Long paqueteIdExcluido) {
        Integer orden = paquete.getOrden();
        if (orden != null
                && !bindingResult.hasFieldErrors("orden")
                && paqueteService.ordenEnUso(orden, paqueteIdExcluido)) {
            bindingResult.rejectValue("orden", "orden.duplicate",
                    "Este número de orden ya está asignado a otro paquete.");
        }

        validarBadges(paquete, bindingResult);
        validarImagen(paquete, bindingResult, creando);
    }

    private void validarBadges(PaqueteForm paquete, BindingResult bindingResult) {
        String badges = paquete.getBadges();
        if (badges == null || badges.isBlank() || bindingResult.hasFieldErrors("badges")) {
            return;
        }

        String[] elementos = badges.split(",", -1);
        if (elementos.length > 6) {
            bindingResult.rejectValue("badges", "badges.limit",
                    "Puedes agregar como máximo 6 badges.");
            return;
        }
        for (String badge : elementos) {
            int longitud = badge.trim().length();
            if (longitud < 2 || longitud > 30) {
                bindingResult.rejectValue("badges", "badges.invalid",
                        "Cada badge debe tener entre 2 y 30 caracteres y estar separado por comas.");
                return;
            }
        }
    }

    private void validarImagen(PaqueteForm paquete,
                               BindingResult bindingResult,
                               boolean creando) {
        boolean obligatoria = creando
                || paquete.getImagenUrl() == null
                || paquete.getImagenUrl().isBlank();
        String error = ImageFileValidator.validar(paquete.getImagen(), obligatoria);
        if (error != null) {
            bindingResult.rejectValue("imagen", "imagen.invalid", error);
        }
    }
}
