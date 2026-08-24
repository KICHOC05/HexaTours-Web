package com.codeboost.travel_agency_backend_template.controller;

import com.codeboost.travel_agency_backend_template.domain.model.Paquete;
import com.codeboost.travel_agency_backend_template.service.PaqueteService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/dashboard/paquetes")
@PreAuthorize("hasRole('ADMIN')")
public class AdminPaqueteController {

    private final PaqueteService paqueteService;

    public AdminPaqueteController(PaqueteService paqueteService) {
        this.paqueteService = paqueteService;
    }

    /* ── Lista ─────────────────────────────── */
    @GetMapping
    public String lista(Model model) {
        model.addAttribute("paquetes",   paqueteService.listarTodos());
        model.addAttribute("activePage", "paquetes");
        return "admin/paquetes/lista";
    }

    /* ── Formulario CREAR ───────────────────── */
    @GetMapping("/nuevo")
    public String nuevoForm(Model model) {
        model.addAttribute("paquete",    new Paquete());
        model.addAttribute("activePage", "paquetes");
        model.addAttribute("accion",     "crear");
        return "admin/paquetes/form";
    }

    /* ── POST CREAR ─────────────────────────── */
    @PostMapping("/nuevo")
    public String crear(
            @ModelAttribute Paquete paquete,
            @RequestParam(required = false) MultipartFile imagen,
            RedirectAttributes ra) {
        try {
            paqueteService.guardar(paquete, imagen);
            ra.addFlashAttribute("success", "Paquete creado correctamente.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al crear el paquete: " + e.getMessage());
        }
        return "redirect:/dashboard/paquetes";
    }

    /* ── Formulario EDITAR ──────────────────── */
    @GetMapping("/{id}/editar")
    public String editarForm(@PathVariable Long id, Model model,
                             RedirectAttributes ra) {
        return paqueteService.buscarPorId(id).map(p -> {
            model.addAttribute("paquete",    p);
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
            @ModelAttribute Paquete paquete,
            @RequestParam(required = false) MultipartFile imagen,
            RedirectAttributes ra) {
        try {
            paqueteService.actualizar(id, paquete, imagen);
            ra.addFlashAttribute("success", "Paquete actualizado correctamente.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al actualizar: " + e.getMessage());
        }
        return "redirect:/dashboard/paquetes";
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
}