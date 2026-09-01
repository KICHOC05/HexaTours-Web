function initLoginForm() {
    const form =
        document.getElementById(
            'login-form'
        );

    if (!form) {
        return;
    }

    const email =
        document.getElementById(
            'email'
        );

    const password =
        document.getElementById(
            'password'
        );

    const emailError =
        document.getElementById(
            'email-error'
        );

    const passwordError =
        document.getElementById(
            'password-error'
        );

    const formError =
        document.getElementById(
            'form-error'
        );

    const setFieldError = (
        input,
        errorElement,
        hasError
    ) => {
        input.classList.toggle(
            'is-invalid',
            hasError
        );

        if (errorElement) {
            errorElement.hidden =
                !hasError;
        }
    };

    form.addEventListener(
        'submit',
        event => {
            email.value =
                email.value.trim().toLowerCase();

            const emailInvalid =
                !email.value
                || !email.validity.valid;

            const passwordEmpty =
                !password.value.trim();

            setFieldError(
                email,
                emailError,
                emailInvalid
            );

            setFieldError(
                password,
                passwordError,
                passwordEmpty
            );

            if (emailInvalid || passwordEmpty) {
                event.preventDefault();

                if (formError) {
                    formError.hidden = false;
                }

                (emailInvalid ? email : password).focus();
            }
        }
    );

    [email, password].forEach(input => {
        input.addEventListener('input', () => {
            input.classList.remove('is-invalid');

            if (formError) {
                formError.hidden = true;
            }
        });
    });
}

function initPasswordToggle() {
    const toggle =
        document.getElementById(
            'password-toggle'
        );

    const password =
        document.getElementById(
            'password'
        );

    if (!toggle || !password) {
        return;
    }

    toggle.addEventListener('click', () => {
        const willShow =
            password.type === 'password';

        password.type =
            willShow ? 'text' : 'password';

        toggle.setAttribute(
            'aria-pressed',
            String(willShow)
        );

        toggle.setAttribute(
            'aria-label',
            willShow
                ? 'Ocultar contraseña'
                : 'Mostrar contraseña'
        );

        toggle.textContent =
            willShow ? 'Ocultar' : 'Ver';
    });
}

function init() {
    initLoginForm();
    initPasswordToggle();
}

document.addEventListener(
    'DOMContentLoaded',
    init
);
