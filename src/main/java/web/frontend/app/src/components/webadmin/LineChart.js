import { Line } from "vue-chartjs";

export default {
    extends: Line,
    props: ['chartData'],
    watch: {
        chartData: function () {
            this.renderChart(
                {
                    labels: Object.keys(this.chartData).map(epoch => new Date(+epoch).toLocaleTimeString()),
                    datasets: [
                        {
                            label: "# players",
                            data: Object.values(this.chartData),
                            backgroundColor: "transparent",
                            borderColor: "rgba(1, 116, 188, 0.50)",
                            pointBackgroundColor: "rgba(171, 71, 188, 1)"
                        }
                    ]
                },
                {
                    scales: {
                        yAxes: [{
                            ticks: {
                                reverse: false,
                                stepSize: 1,
                                min: 0,
                                max: Math.max(...Object.values(this.chartData).map(count => +count)) + 1
                            },
                            gridLines: {
                                display: false
                            }
                        }],
                    },
                    responsive: true,
                    maintainAspectRatio: false,
                    title: {
                        display: true,
                        text: "Players over time"
                    }
                }
            );
        }
    }
};
