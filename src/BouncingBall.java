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
                field.canMove();
                synchronized (field) {
                    List<BouncingBall> ballsCopy = new ArrayList<>(field.getBalls());
                    for (BouncingBall ball : ballsCopy) {
                        if (this != ball && checkCollision(this, ball)) {
                            resolveCollision(this, ball);
                        }
                    }
                }
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



    // Метод для проверки столкновения двух шаров
    private boolean checkCollision(BouncingBall ball1, BouncingBall ball2) {
        double dx = ball1.x - ball2.x;
        double dy = ball1.y - ball2.y;
        double distance = Math.sqrt(dx * dx + dy * dy);
        return distance < ball1.radius + ball2.radius;
    }

    // Метод для реагирования на столкновение двух шаров
    private void resolveCollision(BouncingBall ball1, BouncingBall ball2) {
        double dx = ball1.x - ball2.x;
        double dy = ball1.y - ball2.y;
        double distance = Math.sqrt(dx * dx + dy * dy);

        // Нормализация вектора столкновения
        double nx = dx / distance;
        double ny = dy / distance;

        // Расчет относительной скорости
        double dvx = ball1.speedX - ball2.speedX;
        double dvy = ball1.speedY - ball2.speedY;

        // Проверка, движутся ли шары навстречу друг другу
        if (dvx * nx + dvy * ny >= 0) return;

        // Расчет скорости после столкновения
        double impulse = 30 * (dvx * nx + dvy * ny) / (ball1.radius + ball2.radius);

        ball1.speedX -= impulse * nx;
        ball1.speedY -= impulse * ny;
        ball2.speedX += impulse * nx;
        ball2.speedY += impulse * ny;

        // Коррекция позиции, чтобы предотвратить "залипание" шаров
        double overlap = (ball1.radius + ball2.radius - distance) / 2;
        ball1.x += overlap * nx;
        ball1.y += overlap * ny;
        ball2.x -= overlap * nx;
        ball2.y -= overlap * ny;

//        // Добавление небольшого отталкивания после столкновения
       double repulsion = 4; // Коэффициент отталкивания, можно настроить
        ball1.x += repulsion * nx;
        ball1.y += repulsion * ny;
        ball2.x -= repulsion * nx;
        ball2.y -= repulsion * ny;
    }



    public void paint(Graphics2D canvas) {
        canvas.setColor(color);
        canvas.setPaint(color);
        Ellipse2D.Double ball = new Ellipse2D.Double(x-radius, y-radius,2*radius, 2*radius);
        canvas.draw(ball);
        canvas.fill(ball);
    }
}