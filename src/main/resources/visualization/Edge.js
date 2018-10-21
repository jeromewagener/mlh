class Edge {
    constructor(name, startNeuron, stopNeuron, value) {
        this.name = name;
        this.startNeuron = startNeuron;
        this.stopNeuron = stopNeuron;

        this.value = value;
    }

    draw(canvas) {
        let context = canvas.getContext("2d");

        let hexValue = Number(parseInt( (1.0-this.value) * 255 , 10)).toString(16);

        //debugger;

        //context.strokeStyle="#" + hexValue + hexValue + hexValue;

        if (this.value > 0.95) {
            context.strokeStyle="#" + hexValue + hexValue + hexValue;
            let xPos = this.stopNeuron.x - ((this.stopNeuron.x-this.startNeuron.x) / 2);
            let yPos = this.stopNeuron.y - ((this.stopNeuron.y-this.startNeuron.y) / 2);

            context.fillText(this.value, xPos, yPos);
        } else {
            context.strokeStyle="#e9e0e5";
        }

        context.beginPath();
        context.moveTo(this.startNeuron.x, this.startNeuron.y);
        context.lineTo(this.stopNeuron.x, this.stopNeuron.y);
        context.stroke();
    }
}