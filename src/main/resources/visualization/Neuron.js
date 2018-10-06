class Neuron {
    constructor(name, x, y, radius) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.radius = radius
    }

    draw(canvas) {
        let context = canvas.getContext("2d");
        context.strokeStyle="#000000";
        context.beginPath();
        context.arc(this.x, this.y, this.radius, 0, 2 * Math.PI);
        context.stroke();
        context.font = "12px Arial";
        context.fillText(this.name, this.x + this.radius + 4, this.y + this.radius + 1);
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