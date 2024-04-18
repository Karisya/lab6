import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.util.List;

public class Field extends JPanel {
    // Флаг приостановленности движения
    private boolean paused;
    private boolean pausedBig;
    // Динамический список скачущих мячей
    private final ArrayList<BouncingBall> balls = new ArrayList<>(10);

    // Метод для получения списка шаров


    public Field() {
// Установить цвет заднего фона белым
        setBackground(Color.WHITE);
// Запустить таймер
        // Класс таймер отвечает за регулярную генерацию событий ActionEvent // При создании его экземпляра используется анонимный класс,
        // реализующий интерфейс ActionListener
        // Задача обработчика события ActionEvent - перерисовка окна
        Timer repaintTimer = new Timer(10, ev -> {
// Задача обработчика события ActionEvent - перерисовка окна
            repaint();
        });
        repaintTimer.start();
    }
    // Унаследованный от JPanel метод перерисовки компонента
    public void paintComponent(Graphics g) {
// Вызвать версию метода, унаследованную от предка
        super.paintComponent(g);
        Graphics2D canvas = (Graphics2D) g;
// Последовательно запросить прорисовку от всех мячей из списка
        for (BouncingBall ball: balls) {
            ball.paint(canvas);
        }
    }
    // Метод добавления нового мяча в список
    public synchronized void addBall() {
//Заключается в добавлении в список нового экземпляра BouncingBall // Всю инициализацию положения, скорости, размера, цвета
// BouncingBall выполняет сам в конструкторе
        balls.add(new BouncingBall(this));}
//Thread.getAllStackTraces().keySet().forEach(t -> System.out.println(t.getName()));


    // Метод синхронизированный, т.е. только один поток может // одновременно быть внутри
    public synchronized void pause() {
        paused = true;
    }
    public synchronized void pauseBig() {
        // Включить режим паузы
        pausedBig = true;
    }

// Включить режим паузы

    // Метод синхронизированный, т.е. только один поток может // одновременно быть внутри
    public synchronized void resume() {
    // Выключить режим паузы
        paused = false;
        pausedBig = false;
    // Будим все ожидающие продолжения потоки
        notifyAll();

    }
    // Синхронизированный метод проверки, может ли мяч двигаться // (не включен ли режим паузы?)
    public synchronized void canMove(BouncingBall ball) throws InterruptedException {
        // Проверяем, находится ли игра в режиме паузы
        if (paused) {
            wait();
        }
        if (pausedBig && ball.getRadius() >= 30)
        {
            wait();
        }
    }
}



