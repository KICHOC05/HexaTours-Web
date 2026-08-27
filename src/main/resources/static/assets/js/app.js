const WHATSAPP_NUMBER = '528134215892';
const DESKTOP_TRIPS_PER_PAGE = 6;

function initHeroVideo() {
    const video =
        document.querySelector('[data-hero-video]');

    if (!video) {
        return;
    }

    const playVideo = () => {
        video.muted = true;
        video.defaultMuted = true;
        video.playsInline = true;

        const playback = video.play();

        if (playback) {
            playback.catch(() => {
                // El poster permanece visible cuando el navegador
                // bloquea autoplay por ahorro de energía o datos.
            });
        }
    };

    const resumeOnInteraction = () => {
        playVideo();
        document.removeEventListener(
            'touchstart',
            resumeOnInteraction
        );
        document.removeEventListener(
            'pointerdown',
            resumeOnInteraction
        );
    };

    if (video.readyState >= 2) {
        playVideo();
    } else {
        video.addEventListener(
            'canplay',
            playVideo,
            { once: true }
        );
    }

    window.addEventListener('pageshow', playVideo);
    document.addEventListener('visibilitychange', () => {
        if (!document.hidden) {
            playVideo();
        }
    });
    document.addEventListener(
        'touchstart',
        resumeOnInteraction,
        { passive: true }
    );
    document.addEventListener(
        'pointerdown',
        resumeOnInteraction,
        { passive: true }
    );
}

function initHeader() {
    const header = document.getElementById('site-header');
    const toggle = document.getElementById('menu-toggle');
    const menu = document.getElementById('mobile-menu');

    if (!header || !toggle || !menu) {
        return;
    }

    const setHeaderState = () => {
        header.classList.toggle(
            'is-scrolled',
            window.scrollY > 24
        );
    };

    const closeMenu = () => {
        header.classList.remove('menu-open');

        toggle.setAttribute(
            'aria-expanded',
            'false'
        );

        toggle.setAttribute(
            'aria-label',
            'Abrir menú'
        );

        menu.setAttribute(
            'aria-hidden',
            'true'
        );

        document.body.classList.remove(
            'overflow-hidden'
        );
    };

    setHeaderState();

    window.addEventListener(
        'scroll',
        setHeaderState,
        { passive: true }
    );

    toggle.addEventListener('click', () => {
        const willOpen =
            !header.classList.contains('menu-open');

        header.classList.toggle(
            'menu-open',
            willOpen
        );

        toggle.setAttribute(
            'aria-expanded',
            String(willOpen)
        );

        toggle.setAttribute(
            'aria-label',
            willOpen ? 'Cerrar menú' : 'Abrir menú'
        );

        menu.setAttribute(
            'aria-hidden',
            String(!willOpen)
        );

        document.body.classList.toggle(
            'overflow-hidden',
            willOpen
        );
    });

    menu.querySelectorAll('a').forEach(link => {
        link.addEventListener(
            'click',
            closeMenu
        );
    });
}

function initMobileTripsCarousel() {
    const track =
        document.getElementById('trips-grid');

    const previous =
        document.getElementById('trips-prev');

    const next =
        document.getElementById('trips-next');

    const dots =
        document.getElementById('trip-dots');

    const counter =
        document.getElementById('trip-mobile-counter');

    if (!track || !previous || !next || !dots) {
        return;
    }

    const cards = [
        ...track.querySelectorAll('.trip-card')
    ];

    if (!cards.length) {
        const controls =
            previous.closest('.mobile-carousel-controls');

        if (controls) {
            controls.hidden = true;
        }

        return;
    }

    dots.innerHTML = cards
        .map((card, index) => {
            const trigger =
                card.querySelector('[data-title]');

            const title =
                trigger?.dataset.title ||
                `viaje ${index + 1}`;

            return `
                <button
                    class="carousel-dot${index === 0 ? ' is-active' : ''}"
                    type="button"
                    data-slide="${index}"
                    aria-label="Ver ${title}"
                    aria-current="${index === 0 ? 'true' : 'false'}">
                </button>
            `;
        })
        .join('');

    const dotButtons = [
        ...dots.querySelectorAll('.carousel-dot')
    ];

    let activeIndex = 0;
    let scrollTimer;

    const isMobile = () =>
        window.matchMedia(
            '(max-width: 639px)'
        ).matches;

    const updateControls = index => {
        activeIndex = Math.max(
            0,
            Math.min(index, cards.length - 1)
        );

        previous.disabled =
            activeIndex === 0;

        next.disabled =
            activeIndex === cards.length - 1;

        if (counter) {
            counter.textContent =
                `${String(activeIndex + 1).padStart(2, '0')} / ` +
                `${String(cards.length).padStart(2, '0')}`;
        }

        dotButtons.forEach((dot, dotIndex) => {
            const active =
                dotIndex === activeIndex;

            dot.classList.toggle(
                'is-active',
                active
            );

            dot.setAttribute(
                'aria-current',
                String(active)
            );
        });
    };

    const goToSlide = index => {
        if (!isMobile() || !cards[index]) {
            return;
        }

        const trackStyles =
            window.getComputedStyle(track);

        const inlinePadding =
            Number.parseFloat(
                trackStyles.paddingLeft
            ) || 0;

        const targetLeft =
            cards[index].getBoundingClientRect().left
            - track.getBoundingClientRect().left
            + track.scrollLeft
            - inlinePadding;

        track.scrollTo({
            left: targetLeft,
            behavior: 'smooth'
        });

        updateControls(index);
    };

    previous.addEventListener('click', () => {
        goToSlide(activeIndex - 1);
    });

    next.addEventListener('click', () => {
        goToSlide(activeIndex + 1);
    });

    dotButtons.forEach((dot, index) => {
        dot.addEventListener('click', () => {
            goToSlide(index);
        });
    });

    track.addEventListener(
        'scroll',
        () => {
            if (!isMobile()) {
                return;
            }

            window.clearTimeout(scrollTimer);

            scrollTimer = window.setTimeout(() => {
                const trackLeft =
                    track.getBoundingClientRect().left;

                const closestIndex =
                    cards.reduce(
                        (closest, card, index) => {
                            const currentDistance =
                                Math.abs(
                                    card.getBoundingClientRect().left
                                    - trackLeft
                                );

                            const closestDistance =
                                Math.abs(
                                    cards[closest]
                                        .getBoundingClientRect()
                                        .left
                                    - trackLeft
                                );

                            return currentDistance < closestDistance
                                ? index
                                : closest;
                        },
                        0
                    );

                updateControls(closestIndex);
            }, 80);
        },
        { passive: true }
    );

    const mobileQuery =
        window.matchMedia(
            '(max-width: 639px)'
        );

    mobileQuery.addEventListener(
        'change',
        event => {
            if (!event.matches) {
                return;
            }

            track.scrollLeft = 0;

            updateControls(0);
        }
    );

    updateControls(0);
}

function initDesktopTripsPagination() {
    const grid =
        document.getElementById('trips-grid');

    const pagination =
        document.getElementById('trips-pagination');

    const status =
        document.getElementById('trips-page-status');

    const buttons =
        document.getElementById('trips-page-buttons');

    if (!grid || !pagination || !status || !buttons) {
        return;
    }

    const cards = [
        ...grid.querySelectorAll('.trip-card')
    ];

    const pageCount =
        Math.ceil(
            cards.length / DESKTOP_TRIPS_PER_PAGE
        );

    const mobileQuery =
        window.matchMedia(
            '(max-width: 639px)'
        );

    let currentPage = 1;

    const renderButtons = () => {
        const pageButtons =
            Array.from(
                { length: pageCount },
                (_, index) => {
                    const page = index + 1;

                    const activeClass =
                        page === currentPage
                            ? ' is-active'
                            : '';

                    const currentAttribute =
                        page === currentPage
                            ? 'aria-current="page"'
                            : '';

                    return `
                        <button
                            class="trips-page-button${activeClass}"
                            type="button"
                            data-page="${page}"
                            aria-label="Ir a la página ${page}"
                            ${currentAttribute}>
                            ${page}
                        </button>
                    `;
                }
            ).join('');

        buttons.innerHTML = `
            <button
                class="trips-page-button"
                type="button"
                data-page="${currentPage - 1}"
                aria-label="Página anterior"
                ${currentPage === 1 ? 'disabled' : ''}>
                ←
            </button>

            ${pageButtons}

            <button
                class="trips-page-button"
                type="button"
                data-page="${currentPage + 1}"
                aria-label="Página siguiente"
                ${currentPage === pageCount ? 'disabled' : ''}>
                →
            </button>
        `;
    };

    const applyPage = () => {
        const mobile =
            mobileQuery.matches;

        const firstIndex =
            (currentPage - 1)
            * DESKTOP_TRIPS_PER_PAGE;

        const lastIndex =
            firstIndex
            + DESKTOP_TRIPS_PER_PAGE;

        cards.forEach((card, index) => {
            const hidden =
                !mobile
                && (
                    index < firstIndex
                    || index >= lastIndex
                );

            card.classList.toggle(
                'is-page-hidden',
                hidden
            );
        });

        pagination.hidden =
            mobile || pageCount <= 1;

        if (mobile || pageCount <= 1) {
            return;
        }

        const visibleEnd =
            Math.min(
                lastIndex,
                cards.length
            );

        status.textContent =
            `Mostrando ${firstIndex + 1}–${visibleEnd} ` +
            `de ${cards.length} paquetes`;

        renderButtons();
    };

    buttons.addEventListener('click', event => {
        const trigger =
            event.target.closest('[data-page]');

        if (!trigger || trigger.disabled) {
            return;
        }

        const requestedPage =
            Number.parseInt(
                trigger.dataset.page,
                10
            );

        if (
            !Number.isInteger(requestedPage)
            || requestedPage < 1
            || requestedPage > pageCount
            || requestedPage === currentPage
        ) {
            return;
        }

        currentPage = requestedPage;

        applyPage();

        window.requestAnimationFrame(() => {
            grid.scrollIntoView({
                behavior: 'smooth',
                block: 'start'
            });
        });
    });

    mobileQuery.addEventListener(
        'change',
        () => {
            currentPage = 1;
            applyPage();
        }
    );

    applyPage();
}

function initTripModal() {
    const modal =
        document.getElementById('trip-modal');

    const grid =
        document.getElementById('trips-grid');

    if (!modal || !grid) {
        return;
    }

    const closeButton =
        modal.querySelector('.modal-close');

    const image =
        document.getElementById('modal-image');

    const title =
        document.getElementById('modal-title');

    const price =
        document.getElementById('modal-price');

    const badges =
        document.getElementById('modal-badges');

    const destination =
        document.getElementById('modal-countries');

    const location =
        document.getElementById('modal-cities');

    const description =
        document.getElementById('modal-itinerary');

    const whatsapp =
        document.getElementById('modal-whatsapp');

    grid.addEventListener('click', event => {
        const trigger =
            event.target.closest('[data-trip-id]');

        if (!trigger) {
            return;
        }

        const tripTitle =
            trigger.dataset.title ||
            'este paquete';

        const rawPrice =
            trigger.dataset.price || '';

        const tripDestination =
            trigger.dataset.destination ||
            'Consultar con un asesor';

        const configuredMessage =
            trigger.dataset.waMessage?.trim();

        image.src =
            trigger.dataset.image || '';

        image.alt =
            tripTitle;

        title.textContent =
            tripTitle;

        price.textContent =
            rawPrice
                ? `Desde $${rawPrice} MXN`
                : 'Consultar precio';

        if (badges) {
            const tripBadges = (trigger.dataset.badges || '')
                .split(',')
                .map(badge => badge.trim())
                .filter(Boolean);

            badges.replaceChildren();

            tripBadges.forEach(badge => {
                const item = document.createElement('span');
                item.textContent = badge;
                badges.appendChild(item);
            });

            badges.hidden = tripBadges.length === 0;
        }

        destination.textContent =
            tripDestination;

        location.textContent =
            tripDestination;

        description.textContent =
            trigger.dataset.description ||
            'Solicita la información completa con uno de nuestros asesores.';

        const message = configuredMessage || [
                'Hola, HexaTours.',
                '',
                `Me interesa el paquete: ${tripTitle}.`,
                '¿Podrían compartir disponibilidad, itinerario y tarifa vigente?',
                '',
                'Quedo atento(a) a su respuesta.'
            ].join('\n');

        whatsapp.href =
            `https://wa.me/${WHATSAPP_NUMBER}` +
            `?text=${encodeURIComponent(message)}`;

        modal.showModal();

        document.body.classList.add(
            'overflow-hidden'
        );
    });

    const closeModal = () => {
        modal.close();

        document.body.classList.remove(
            'overflow-hidden'
        );
    };

    closeButton?.addEventListener(
        'click',
        closeModal
    );

    modal.addEventListener(
        'click',
        event => {
            if (event.target === modal) {
                closeModal();
            }
        }
    );

    modal.addEventListener(
        'close',
        () => {
            document.body.classList.remove(
                'overflow-hidden'
            );
        }
    );
}

function initContactForm() {
    const form =
        document.getElementById('contact-form');

    const status =
        document.getElementById('form-status');

    if (!form || !status) {
        return;
    }

    form.addEventListener('submit', event => {
        event.preventDefault();

        if (!form.checkValidity()) {
            form.reportValidity();

            status.textContent =
                'Por favor completa los campos obligatorios.';

            return;
        }

        const data =
            new FormData(form);

        const nombre =
            data.get('nombre') || 'viajero';

        const correo =
            data.get('correo') || '';

        const mensaje =
            data.get('mensaje') || '';

        const whatsappMessage = [
            'Hola, HexaTours.',
            '',
            'Me gustaría recibir información para planear mi próximo viaje.',
            '',
            `Nombre: ${nombre}`,
            `Correo: ${correo || 'No proporcionado'}`,
            '',
            'Destino o solicitud:',
            mensaje,
            '',
            'Quedo atento(a) a su respuesta.'
        ].join('\n');

        const whatsappUrl =
            `https://wa.me/${WHATSAPP_NUMBER}` +
            `?text=${encodeURIComponent(whatsappMessage)}`;

        window.open(
            whatsappUrl,
            '_blank',
            'noopener'
        );

        status.textContent =
            `Gracias, ${nombre}. Te redirigimos a WhatsApp.`;

        form.reset();
    });
}

function initRevealAnimations() {
    const reduceMotion =
        window.matchMedia(
            '(prefers-reduced-motion: reduce)'
        ).matches;

    if (
        reduceMotion
        || !('IntersectionObserver' in window)
    ) {
        document
            .querySelectorAll('.reveal')
            .forEach(element => {
                element.classList.add(
                    'is-visible'
                );
            });

        return;
    }

    const observer =
        new IntersectionObserver(
            entries => {
                entries.forEach(entry => {
                    if (entry.isIntersecting) {
                        entry.target.classList.add(
                            'is-visible'
                        );

                        observer.unobserve(
                            entry.target
                        );
                    }
                });
            },
            {
                threshold: 0.12,
                rootMargin: '0px 0px -40px'
            }
        );

    document
        .querySelectorAll('.reveal')
        .forEach(element => {
            observer.observe(element);
        });
}

function initServiceAnimations() {
    const services =
        document.querySelectorAll(
            '.service-reveal'
        );

    if (!services.length) {
        return;
    }

    const reduceMotion =
        window.matchMedia(
            '(prefers-reduced-motion: reduce)'
        ).matches;

    if (reduceMotion) {
        services.forEach(service => {
            service.classList.add(
                'is-visible'
            );
        });

        return;
    }

    const observer =
        new IntersectionObserver(
            entries => {
                entries.forEach(entry => {
                    if (entry.isIntersecting) {
                        entry.target.classList.add(
                            'is-visible'
                        );

                        observer.unobserve(
                            entry.target
                        );
                    }
                });
            },
            {
                threshold: 0.20,
                rootMargin: '0px 0px -60px 0px'
            }
        );

    services.forEach(service => {
        observer.observe(service);
    });
}

function initServicesCarousel() {
    const carousel =
        document.getElementById(
            'services-carousel'
        );

    if (!carousel) {
        return;
    }

    const reduceMotion =
        window.matchMedia(
            '(prefers-reduced-motion: reduce)'
        ).matches;

    if (reduceMotion) {
        return;
    }

    const originals =
        Array.from(carousel.children);

    originals.forEach(card => {
        const clone =
            card.cloneNode(true);

        clone.classList.remove(
            'service-reveal'
        );

        clone.classList.add(
            'is-visible'
        );

        carousel.appendChild(clone);
    });

    let setWidth =
        carousel.scrollWidth / 2;

    const speed = 1.1;

    const tick = () => {
        if (carousel.scrollLeft >= setWidth) {
            carousel.scrollLeft -= setWidth;
        } else {
            carousel.scrollLeft += speed;
        }

        window.requestAnimationFrame(tick);
    };

    window.requestAnimationFrame(tick);

    window.addEventListener('resize', () => {
        setWidth =
            carousel.scrollWidth / 2;
    });
}

function init() {
    initHeroVideo();
    initHeader();
    initMobileTripsCarousel();
    initDesktopTripsPagination();
    initTripModal();
    initContactForm();
    initRevealAnimations();
    initServiceAnimations();
    initServicesCarousel();

    const year =
        document.getElementById(
            'current-year'
        );

    if (year) {
        year.textContent =
            new Date().getFullYear();
    }
}

document.addEventListener(
    'DOMContentLoaded',
    init
);
