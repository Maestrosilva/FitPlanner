document.addEventListener('DOMContentLoaded', () => {
    const unitSelect = document.querySelector('select[name="measuringUnits"]');
    const weightInput = document.querySelector('input[name="weight"]');

    const KG_TO_LB =2.20462;
    if (unitSelect && weightInput) {
        unitSelect.addEventListener('change', function() {
            let currentWeight = parseFloat(weightInput.value);
            if (isNaN(currentWeight)) return;

            if (this.value === 'lbs') {
                weightInput.value = (currentWeight * KG_TO_LB).toFixed(1);
            } else {
                weightInput.value = (currentWeight / KG_TO_LB).toFixed(1);
            }

            const label = weightInput.closest('div').querySelector('label');
            if (label) {
                label.textContent = `Weight (${this.value})`;
            }
        });
    }
});