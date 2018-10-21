class Neuron {
    constructor(name, x, y, radius, value, bias) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.radius = radius;

        this.value = value;
        this.bias = bias;
    }

    draw(canvas) {
        //debugger;
        let hexValue = Number(parseInt( (1.0-this.value) * 255 , 10)).toString(16);
        let context = canvas.getContext("2d");
        context.strokeStyle="#CCCCCC";
        context.fillStyle="#" + hexValue + hexValue + hexValue;
        context.beginPath();
        context.arc(this.x, this.y, this.radius, 0, 2 * Math.PI);
        context.fill();
        context.stroke();
        context.font = "12px Arial";

        if (this.name.startsWith("I")) {
            context.fillText(this.name + " (" + this.value + (this.bias !== undefined ? " - " + this.bias : "") + ")", this.x + this.radius - 80, this.y + this.radius + 1);
        } else {
            context.fillText(this.name + " (" + this.value + (this.bias !== undefined ? " - " + this.bias : "") + ")", this.x + this.radius + 4, this.y + this.radius + 1);
        }
    }

    isAtCoordinates(x, y) {
        return this.x - this.radius <= x && this.x + this.radius >= x &&
            this.y - this.radius <= y && this.y + this.radius >= y;


    }

    highlight(canvas) {
        let context = canvas.getContext("2d");
        context.strokeStyle="#cbcc44";
        context.beginPath();
        context.arc(this.x, this.y, this.radius, 0, 2 * Math.PI);
        context.fill();
        context.stroke();
    }
}