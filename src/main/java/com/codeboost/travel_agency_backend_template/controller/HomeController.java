package com.codeboost.travel_agency_backend_template.controller;

import com.codeboost.travel_agency_backend_template.service.PaqueteService;
import com.codeboost.travel_agency_backend_template.service.UsuarioService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final UsuarioService usuarioService;
    private final PaqueteService paqueteService;

    public HomeController(UsuarioService usuarioService,
                          PaqueteService paqueteService) {
        this.usuarioService = usuarioService;
        this.paqueteService = paqueteService;
    }

    /* ── Página pública ── */
    @GetMapping("/")
    public String index(
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {

        if (userDetails != null) {
            usuarioService.buscarPorEmail(userDetails.getUsername())
                          .ifPresent(u -> model.addAttribute("usuario", u));
        }
        model.addAttribute("experiencias", paqueteService.listarActivos());
        return "index";
    }

    /* ── Aviso de privacidad ── */
    @GetMapping("/privacidad")
    public String privacidad() {
        return "privacidad";
    }
}