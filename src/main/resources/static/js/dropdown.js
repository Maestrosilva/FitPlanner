const genderSelect = document.querySelector('.gender-select');
const genderButton = document.querySelector('.gender-button');
const genderLabels = document.querySelectorAll('.gender-options label');

const experienceSelect = document.querySelector('.experience-select');
const experienceButton = document.querySelector('.experience-button');
const experienceLabels = document.querySelectorAll('.experience-options label');

function toggleOpen(container, button, open) {
    if (open) {
        container.classList.add('open');
        button.setAttribute('aria-expanded', 'true');
    } else {
        container.classList.remove('open');
        button.setAttribute('aria-expanded', 'false');
    }
}

document.addEventListener('click', (e) => {
    if (!genderSelect.contains(e.target)) toggleOpen(genderSelect, genderButton, false);
    if (!experienceSelect.contains(e.target)) toggleOpen(experienceSelect, experienceButton, false);
});

genderButton.addEventListener('click', (e) => {
    e.stopPropagation();
    toggleOpen(genderSelect, genderButton, !genderSelect.classList.contains('open'));
});

genderLabels.forEach(label => {
    label.addEventListener('click', (e) => {
        const inputId = label.htmlFor;
        const radio = document.getElementById(inputId);
        if (radio) radio.checked = true;

        genderButton.textContent = label.textContent;
        toggleOpen(genderSelect, genderButton, false);
    });
});

experienceButton.addEventListener('click', (e) => {
    e.stopPropagation();
    toggleOpen(experienceSelect, experienceButton, !experienceSelect.classList.contains('open'));
});

experienceLabels.forEach(label => {
    label.addEventListener('click', (e) => {
        const inputId = label.htmlFor;
        const radio = document.getElementById(inputId);
        if (radio) radio.checked = true;

        experienceButton.textContent = label.textContent;
        toggleOpen(experienceSelect, experienceButton, false);
    });
});

const initRadioToButton = (labels, button) => {
    labels.forEach(label => {
        const input = document.getElementById(label.htmlFor);
        if (input && input.checked) button.textContent = label.textContent;
    });
};
initRadioToButton(genderLabels, genderButton);
initRadioToButton(experienceLabels, experienceButton);