import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.random;
import static java.lang.Math.round;

public class BouncingBall implements Runnable {
    private static final int MAX_RADIUS = 40;
    private static final int MIN_RADIUS = 3;
    private static final int MAX_SPEED = 15;
    private final Field field;

    private final int radius;

    private final Color color;
    // Текущие координаты мяча
    private double x;
    private double y;
    // Вертикальная и горизонтальная компонента скорости
    private int speed;
    private double speedX;
    private double speedY;
    // Конструктор класса BouncingBall
    public BouncingBall(Field field) {

// через getWidth(), getHeight()
        this.field = field;

        radius = (int)(random()*(MAX_RADIUS - MIN_RADIUS)) + MIN_RADIUS;

        speed = Double.valueOf(round((float) (5 * MAX_SPEED) / radius)).intValue();
        if (speed>MAX_SPEED) {speed = MAX_SPEED;
        }
// Начальное направление скорости тоже случайно,
// угол в пределах от 0 до 2PI
        double angle = random()*2*Math.PI;
// Вычисляются горизонтальная и вертикальная компоненты скорости
        speedX = 3*Math.cos(angle);
        speedY = 3*Math.sin(angle);
// Цвет мяча выбирается случайно
        color = new Color((float) random(), (float) random(),
                (float) random());
// Начальное положение мяча случайно
        x = random()*(field.getSize().getWidth()-2*radius) + radius;
        y = random()*(field.getSize().getHeight()-2*radius) + radius; // Создаѐм новый экземпляр потока, передавая аргументом
// Ссылку на класс, реализующий Runnable (т.е. на себя)
        Thread thisThread = new Thread(this);

        thisThread.start();
    }
    // Метод run() исполняется внутри потока. Когда он завершает работу, // то завершается и поток
    @SuppressWarnings({"BusyWait", "InfiniteLoopStatement"})
    public void run() {
        try {
            while(true) {
                field.canMove(this);

                // Обработка столкновения с краями поля
                if (x + speedX <= radius) {
                    speedX = -speedX;
                    x = radius;
                } else if (x + speedX >= field.getWidth() - radius) {
                    speedX = -speedX;
                    x = field.getWidth() - radius;
                }
                if (y + speedY <= radius) {
                    speedY = -speedY;
                    y = radius;
                } else if (y + speedY >= field.getHeight() - radius) {
                    speedY = -speedY;
                    y = field.getHeight() - radius;
                }
                // Смещение шара
                x += speedX;
                y += speedY;
                // Задержка
                Thread.sleep(16-speed);
            }
        } catch (InterruptedException ignored) {}
    }


    public void paint(Graphics2D canvas) {
        canvas.setColor(color);
        canvas.setPaint(color);
        Ellipse2D.Double ball = new Ellipse2D.Double(x-radius, y-radius,2*radius, 2*radius);
        canvas.draw(ball);
        canvas.fill(ball);
    }


    public int getRadius() {
        return radius;
    }
}