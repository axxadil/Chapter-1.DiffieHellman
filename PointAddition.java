package diffiehellman;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PointAddition extends JFrame {
    private JTextField aField, pField, x1Field, y1Field, x2Field, y2Field;
    private JButton calculateButton;

    public PointAddition() {
        super("Elliptic Curve Point Addition");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 250);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        aField = new JTextField(10);
        pField = new JTextField(10);
        x1Field = new JTextField(10);
        y1Field = new JTextField(10);
        x2Field = new JTextField(10);
        y2Field = new JTextField(10);

        calculateButton = new JButton("Вычислить");
        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculate();
            }
        });

        add(new JLabel("Введите коэффициент a:"));
        add(aField);
        add(new JLabel("Введите простое число p (модуль):"));
        add(pField);
        add(new JLabel("Введите координаты точки P (x1, y1):"));
        add(x1Field);
        add(y1Field);
        add(new JLabel("Введите координаты точки Q (x2, y2):"));
        add(x2Field);
        add(y2Field);
        add(calculateButton);

        setVisible(true);
    }

    private void calculate() {
        try {
            long a = Long.parseLong(aField.getText());
            long p = Long.parseLong(pField.getText());
            long x1 = Long.parseLong(x1Field.getText());
            long y1 = Long.parseLong(y1Field.getText());
            long x2 = Long.parseLong(x2Field.getText());
            long y2 = Long.parseLong(y2Field.getText());

            Point p1 = new Point(x1, y1);
            Point p2 = new Point(x2, y2);

            Point result = addPoints(p1, p2, a, p);
            if (result == null) {
                JOptionPane.showMessageDialog(this, "Результат: Точка на бесконечности");
            } else {
                JOptionPane.showMessageDialog(this, "Результат сложения: (" + result.x + ", " + result.y + ")");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Пожалуйста, введите корректные числовые значения.");
        }
    }

    private Point addPoints(Point p1, Point p2, long a, long p) {
        if (p1.equals(p2) && p1.y == 0) {
            return null;  // касательная в инфинити
        }

        if (p1.equals(p2)) {
            return doublePoint(p1, a, p);
        }

        if (p1.x == p2.x) {
            return null;  // P + (-P) = O
        }

        long lambdaNumerator = mod(p2.y - p1.y, p);
        long lambdaDenominator = modInverse(mod(p2.x - p1.x, p), p);
        long lambda = mod(lambdaNumerator * lambdaDenominator, p);

        long x3 = mod(mod(lambda * lambda, p) - p1.x - p2.x, p);
        long y3 = mod(mod(lambda * (p1.x - x3) - p1.y, p), p);

        return new Point(x3, y3);
    }

    private Point doublePoint(Point p, long a, long p) {
        long lambdaNumerator = mod(3 * p.x * p.x + a, p);
        long lambdaDenominator = modInverse(2 * p.y, p);
        long lambda = mod(lambdaNumerator * lambdaDenominator, p);

        long x3 = mod(lambda * lambda - 2 * p.x, p);
        long y3 = mod(lambda * (p.x - x3) - p.y, p);

        return new Point(x3, y3);
    }

    private long modInverse(long a, long m) {
        long m0 = m, t, q;
        long x0 = 0, x1 = 1;

        if (m == 1)
            return 0;

        while (a > 1) {
            q = a / m;
            t = m;
            m = a % m; 
            a = t;
            t = x0;
            x0 = x1 - q * x0;
            x1 = t;
        }

        if (x1 < 0)
            x1 += m0;

        return x1;
    }

    private long mod(long value, long mod) {
        return ((value % mod) + mod) % mod;
    }

    private static class Point {
        long x, y;

        public Point(long x, long y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Point) {
                Point other = (Point) obj;
                return this.x == other.x && this.y == other.y;
            }
            return false;
        }
    }

    public static void main(String[] args) {
        new PointAddition();
    }
}

