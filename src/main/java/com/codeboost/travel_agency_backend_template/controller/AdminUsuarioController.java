package com.codeboost.travel_agency_backend_template.controller;

import com.codeboost.travel_agency_backend_template.service.UsuarioService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/dashboard/usuarios")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUsuarioController {

    private final UsuarioService usuarioService;

    public AdminUsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /* ── Lista ─────────────────────────────── */
    @GetMapping
    public String lista(Model model) {
        model.addAttribute("usuarios",   usuarioService.listarTodos());
        model.addAttribute("activePage", "usuarios");
        return "admin/usuarios/lista";
    }

    /* ── Formulario CREAR ───────────────────── */
    @GetMapping("/nuevo")
    public String nuevoForm(Model model) {
        model.addAttribute("activePage", "usuarios");
        model.addAttribute("accion", "crear");
        return "admin/usuarios/form";
    }

    /* ── POST CREAR ─────────────────────────── */
    @PostMapping("/nuevo")
    public String crear(
            @RequestParam String nombre,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String rol,
            RedirectAttributes ra) {
        try {
            usuarioService.registrar(nombre, email, password);
            // Actualizar rol si no es el default
            if (!rol.equals("ROLE_USER")) {
                usuarioService.buscarPorEmail(email).ifPresent(u ->
                    usuarioService.actualizar(u.getId(), u.getNombre(),
                                             u.getEmail(), rol, u.isActivo())
                );
            }
            ra.addFlashAttribute("success", "Usuario creado correctamente.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/dashboard/usuarios";
    }

    /* ── Formulario EDITAR ──────────────────── */
    @GetMapping("/{id}/editar")
    public String editarForm(@PathVariable Long id, Model model,
                             RedirectAttributes ra) {
        return usuarioService.buscarPorId(id).map(u -> {
            model.addAttribute("usuario",    u);
            model.addAttribute("activePage", "usuarios");
            model.addAttribute("accion",     "editar");
            return "admin/usuarios/form";
        }).orElseGet(() -> {
            ra.addFlashAttribute("error", "Usuario no encontrado.");
            return "redirect:/dashboard/usuarios";
        });
    }

    /* ── POST EDITAR ────────────────────────── */
    @PostMapping("/{id}/editar")
    public String editar(
            @PathVariable Long id,
            @RequestParam String nombre,
            @RequestParam String email,
            @RequestParam String rol,
            @RequestParam(defaultValue = "false") boolean activo,
            RedirectAttributes ra) {
        try {
            usuarioService.actualizar(id, nombre, email, rol, activo);
            ra.addFlashAttribute("success", "Usuario actualizado correctamente.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/dashboard/usuarios";
    }

    /* ── Cambiar contraseña (admin) ─────────── */
    @GetMapping("/{id}/password")
    public String passwordForm(@PathVariable Long id, Model model,
                               RedirectAttributes ra) {
        return usuarioService.buscarPorId(id).map(u -> {
            model.addAttribute("usuario",    u);
            model.addAttribute("activePage", "usuarios");
            return "admin/usuarios/cambiar-password";
        }).orElseGet(() -> {
            ra.addFlashAttribute("error", "Usuario no encontrado.");
            return "redirect:/dashboard/usuarios";
        });
    }

    /* ── POST Cambiar contraseña (admin) ─────── */
    @PostMapping("/{id}/password")
    public String cambiarPassword(
            @PathVariable Long id,
            @RequestParam String passwordNueva,
            @RequestParam String confirmar,
            RedirectAttributes ra) {
        if (!passwordNueva.equals(confirmar)) {
            ra.addFlashAttribute("error", "Las contraseñas no coinciden.");
            return "redirect:/dashboard/usuarios/" + id + "/password";
        }
        try {
            usuarioService.cambiarPasswordAdmin(id, passwordNueva);
            ra.addFlashAttribute("success", "Contraseña actualizada correctamente.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/dashboard/usuarios";
    }

    /* ── Toggle activo ─────────────────────── */
    @PostMapping("/{id}/toggle")
    public String toggle(@PathVariable Long id, RedirectAttributes ra) {
        try {
            usuarioService.buscarPorId(id).ifPresent(u ->
                usuarioService.actualizar(u.getId(), u.getNombre(),
                                         u.getEmail(), u.getRol(), !u.isActivo())
            );
            ra.addFlashAttribute("success", "Estado del usuario actualizado.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/dashboard/usuarios";
    }

    /* ── Eliminar ───────────────────────────── */
    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes ra) {
        try {
            usuarioService.eliminar(id);
            ra.addFlashAttribute("success", "Usuario eliminado correctamente.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/dashboard/usuarios";
    }
}