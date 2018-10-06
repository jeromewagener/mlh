class Edge {
    constructor(name, startNeuron, stopNeuron) {
        this.name = name;
        this.startNeuron = startNeuron;
        this.stopNeuron = stopNeuron;
    }

    draw(canvas) {
        let context = canvas.getContext("2d");
        context.strokeStyle="#e9e0e5";
        context.beginPath();
        context.moveTo(this.startNeuron.x, this.startNeuron.y);
        context.lineTo(this.stopNeuron.x, this.stopNeuron.y);
        context.stroke();
    }
}