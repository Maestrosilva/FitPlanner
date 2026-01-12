let exerciseProgressChart;
let personalProgressChart;

const userUnit = window.userStatsConfig ? window.userStatsConfig.unit : 'kg';
const personalLabels = window.userStatsConfig ? window.userStatsConfig.personalLabels : [];
const personalData = window.userStatsConfig ? window.userStatsConfig.personalData : [];

const activeExercises = new Map();
const colors = ['#0d6efd', '#dc3545', '#ffc107', '#198754', '#0dcaf0', '#6610f2'];

document.addEventListener('DOMContentLoaded', () => {
    initCharts();
    setupThemeObserver();
});

function getThemeVar(varName) {
    return getComputedStyle(document.documentElement).getPropertyValue(varName).trim();
}

function initCharts() {
    const textColor = getThemeVar('--text-main') || '#000';
    const gridColor = getThemeVar('--border-soft') || 'rgba(0,0,0,0.1)';
    const accentColor = getThemeVar('--success-accent') || '#198754';

    const commonScales = {
        y: {
            grid: { color: gridColor },
            ticks: { color: textColor, maxTicksLimit: 10 },
            title: { display: true, text: `Weight (${userUnit})`, color: textColor, font: { weight: 'bold' } },
            beginAtZero: false
        },
        x: {
            grid: { color: gridColor },
            ticks: { color: textColor },
            title: { display: true, text: 'Date', color: textColor }
        }
    };

    // Exercise chart (dynamic, updates when exercises selected)
    const exCtx = document.getElementById('exerciseProgressChart').getContext('2d');
    exerciseProgressChart = new Chart(exCtx, {
        type: 'line',
        data: { labels: [], datasets: [] },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: commonScales,
            plugins: { legend: { labels: { color: textColor } } }
        }
    });

    // Personal weight chart (historical)
    const pCtx = document.getElementById('personalProgressChart').getContext('2d');
    personalProgressChart = new Chart(pCtx, {
        type: 'line',
        data: {
            labels: personalLabels,
            datasets: [{
                label: `Body Weight (${userUnit})`,
                data: personalData,
                borderColor: accentColor,
                backgroundColor: accentColor + '33',
                fill: true,
                tension: 0.3
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: commonScales,
            plugins: { legend: { labels: { color: textColor } } }
        }
    });
}

function toggleExercise(cardElement) {
    const parent = cardElement.closest('.exercise-item');
    const id = parent.dataset.id;
    const name = parent.dataset.name;

    if (activeExercises.has(id)) {
        activeExercises.delete(id);
        cardElement.classList.remove('selected-active');
        updateExerciseChart();
    } else {
        fetch(`/stats/exercise/${id}`)
            .then(res => res.json())
            .then(data => {
                if (!data.labels || data.labels.length === 0) return;

                const colorIndex = activeExercises.size % colors.length;
                activeExercises.set(id, {
                    label: name,
                    values: data.values,
                    labels: data.labels,
                    color: colors[colorIndex]
                });

                cardElement.classList.add('selected-active');
                updateExerciseChart();
            });
    }
}

function updateExerciseChart() {
    const globalLabelsSet = new Set();
    activeExercises.forEach(ex => ex.labels.forEach(date => globalLabelsSet.add(date)));
    const sortedLabels = Array.from(globalLabelsSet).sort();

    const datasets = [];
    activeExercises.forEach(ex => {
        const alignedData = ex.labels.map((date, index) => ({
            x: date,
            y: ex.values[index]
        }));

        datasets.push({
            label: ex.label,
            data: alignedData,
            borderColor: ex.color,
            backgroundColor: ex.color + '15',
            fill: true,
            tension: 0.3,
            pointRadius: 4
        });
    });

    exerciseProgressChart.data.labels = sortedLabels;
    exerciseProgressChart.data.datasets = datasets;
    exerciseProgressChart.update();

    const counter = document.getElementById('selectedCounter');
    if (counter) counter.innerText = `${activeExercises.size} selected`;
}

function setupThemeObserver() {
    const observer = new MutationObserver(() => {
        if (exerciseProgressChart) exerciseProgressChart.destroy();
        if (personalProgressChart) personalProgressChart.destroy();
        initCharts();
        updateExerciseChart();
    });
    observer.observe(document.documentElement, { attributes: true, attributeFilter: ['class'] });
}