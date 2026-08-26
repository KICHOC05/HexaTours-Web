// AuthController.java
package com.codeboost.travel_agency_backend_template.controller;

import com.codeboost.travel_agency_backend_template.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/login")
    public String loginForm(
            @RequestParam(required = false) String error,
            @RequestParam(required = false) String logout,
            @RequestParam(name = "rate-limited", required = false) String rateLimited,
            Model model) {
        if (error  != null) model.addAttribute("error",  "Correo o contraseña incorrectos.");
        if (logout != null) model.addAttribute("logout", "Sesión cerrada correctamente.");
        if (rateLimited != null) {
            model.addAttribute("error", "Demasiados intentos fallidos. Espera unos minutos e inténtalo nuevamente.");
        }
        return "login";
    }

    @GetMapping("/registro")
    public String registroForm() {
        return "registro";
    }

    @PostMapping("/registro")
    public String registrar(
            @RequestParam String nombre,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String confirmar,
            RedirectAttributes ra) {

        if (nombre.isBlank() || email.isBlank() || password.isBlank()) {
            ra.addFlashAttribute("error", "Todos los campos son obligatorios.");
            return "redirect:/registro";
        }
        if (!password.equals(confirmar)) {
            ra.addFlashAttribute("error", "Las contraseñas no coinciden.");
            return "redirect:/registro";
        }
        if (password.length() < 8) {
            ra.addFlashAttribute("error", "Mínimo 8 caracteres en la contraseña.");
            return "redirect:/registro";
        }
        if (usuarioService.existeEmail(email)) {
            ra.addFlashAttribute("error", "Ese correo ya está registrado.");
            return "redirect:/registro";
        }
        try {
            usuarioService.registrar(nombre, email, password);
            ra.addFlashAttribute("success", "¡Cuenta creada! Ya puedes iniciar sesión.");
            return "redirect:/login";
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error inesperado. Intenta de nuevo.");
            return "redirect:/registro";
        }
    }
}
