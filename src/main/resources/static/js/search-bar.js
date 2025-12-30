document.addEventListener("DOMContentLoaded", function () {
    const searchInputs = document.querySelectorAll(".search-input");
    searchInputs.forEach(searchInput => {
        if (!window.applyExerciseFilters) {
            console.warn("applyExerciseFilters function not found on window.");
            return;
        }
        searchInput.addEventListener("input", function () {
            const query = this.value.toLowerCase().trim();
            window.applyExerciseFilters(query);
        });
    });
});