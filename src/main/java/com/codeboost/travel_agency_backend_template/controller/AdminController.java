package com.codeboost.travel_agency_backend_template.controller;

import com.codeboost.travel_agency_backend_template.service.PaqueteService;
import com.codeboost.travel_agency_backend_template.service.UsuarioService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dashboard")
public class AdminController {

    private final UsuarioService usuarioService;
    private final PaqueteService paqueteService;

    public AdminController(UsuarioService usuarioService,
                           PaqueteService paqueteService) {
        this.usuarioService = usuarioService;
        this.paqueteService = paqueteService;
    }

    /* ── Dashboard admin ── */
    @GetMapping
    public String dashboard(
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {

        usuarioService.buscarPorEmail(userDetails.getUsername())
                      .ifPresent(u -> model.addAttribute("usuario", u));

        model.addAttribute("activePage", "inicio");
        model.addAttribute("totalUsuarios", usuarioService.listarTodos().size());
        model.addAttribute("totalPaquetes", paqueteService.listarTodos().size());
        return "admin/dashboard";
    }
}