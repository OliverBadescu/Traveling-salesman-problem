package proiect;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ComisVoiajorRomania extends JFrame {

    private final String caleImagine = "C:\\Users\\oliba\\Desktop\\proiect\\Comis Voiajor\\src\\proiect\\img\\harta-romania-schimbata-12-judete-tudor-benga-832012.jpg";
    private final List<Oras> orase = Arrays.asList(
            new Oras("București", 725, 625),
            new Oras("Cluj-Napoca", 390, 290),
            new Oras("Timișoara", 150, 390),
            new Oras("Iași", 840, 210),
            new Oras("Constanța", 950, 670),
            new Oras("Alba Iulia", 425, 365),
            new Oras("Arad", 190, 355),
            new Oras("Pitești", 575, 585),
            new Oras("Bacău", 770, 320),
            new Oras("Oradea", 300, 235),
            new Oras("Bistrița", 540, 255),
            new Oras("Botoșani", 760, 150),
            new Oras("Brăila", 870, 500),
            new Oras("Brașov", 605, 430),
            new Oras("Buzău", 800, 545),
            new Oras("Reșița", 265, 490),
            new Oras("Călărași", 840, 650),
            new Oras("Sfântu Gheorghe", 670, 395),
            new Oras("Târgu Mureș", 540, 320),
            new Oras("Craiova", 460, 650),
            new Oras("Drobeta-Turnu Severin", 350, 590),
            new Oras("Galați", 870, 460),
            new Oras("Giurgiu", 680, 700),
            new Oras("Deva", 390, 420),
            new Oras("Slobozia", 840, 600),
            new Oras("Suceava", 700, 165),
            new Oras("Alexandria", 630, 690),
            new Oras("Târgu Jiu", 420, 555),
            new Oras("Miercurea Ciuc", 670, 340),
            new Oras("Satu Mare", 380, 150),
            new Oras("Piatra Neamț", 740, 265),
            new Oras("Târgoviște", 640, 565),
            new Oras("Tulcea", 990, 530),
            new Oras("Vaslui", 865, 300),
            new Oras("Râmnicu Vâlcea", 500, 550),
            new Oras("Focșani", 820, 440),
            new Oras("Zalău", 400, 240),
            new Oras("Sibiu", 520, 430),
            new Oras("Slatina", 530, 630),
            new Oras("Baia Mare", 470, 150),
            new Oras("Ploiesti", 700, 535)

    );

    private final List<Oras> oraseSelectate = new ArrayList<>();
    private final List<int[]> muchii = new ArrayList<>();
    private final JButton butonBacktracking = new JButton("Backtracking");
    private final JButton butonGreedy = new JButton("Greedy");
    private final JLabel mesajLabel = new JLabel("Selecteaza orasele facand click pe harta.");
    private final JLabel distantaLabel = new JLabel("Distanta totala: N/A km");

    private final double pixelLaKm = calculeazaPixelLaKm();

    public ComisVoiajorRomania() {
        setTitle("Comis Voiajor pe Harta Romaniei");
        setSize(1200, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panou = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                deseneazaHarta(g);
            }
        };

        panou.setLayout(new BorderLayout());
        add(panou, BorderLayout.CENTER);

        JPanel panouControl = new JPanel();
        panouControl.add(butonBacktracking);
        panouControl.add(butonGreedy);
        panouControl.add(mesajLabel);
        panouControl.add(distantaLabel);
        add(panouControl, BorderLayout.SOUTH);

        butonBacktracking.addActionListener(e -> rezolvaComisVoiajor("backtracking"));
        butonGreedy.addActionListener(e -> rezolvaComisVoiajor("greedy"));

        panou.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selecteazaOras(e.getX(), e.getY());
                repaint();
            }
        });
    }

    private void deseneazaHarta(Graphics g) {
        Image imagineHarta = new ImageIcon(caleImagine).getImage();

        int x = (getWidth() - imagineHarta.getWidth(this)) / 2;
        int y = (getHeight() - imagineHarta.getHeight(this)) / 2;

        g.drawImage(imagineHarta, x, y-45, this);


        g.setColor(Color.BLUE);
        for (Oras oras : orase) {
            g.fillOval(oras.x - 5, oras.y - 5, 10, 10);
            g.drawString(oras.nume, oras.x - 15, oras.y - 10);
        }

        g.setColor(Color.MAGENTA);
        for (Oras oras : oraseSelectate) {
            g.fillOval(oras.x - 5, oras.y - 5, 10, 10);
            g.drawString(oras.nume, oras.x - 15, oras.y - 10);
        }

        g.setColor(Color.GREEN);
        for (int[] muchie : muchii) {
            Oras oras1 = oraseSelectate.get(muchie[0]);
            Oras oras2 = oraseSelectate.get(muchie[1]);
            g.drawLine(oras1.x, oras1.y, oras2.x, oras2.y);
        }
    }

    private void selecteazaOras(int x, int y) {
        for (Oras oras : orase) {
            if (Math.abs(oras.x - x) <= 10 && Math.abs(oras.y - y) <= 10 && !oraseSelectate.contains(oras)) {
                oraseSelectate.add(oras);
                break;
            }
        }
    }

    private void rezolvaComisVoiajor(String metoda) {
        if (oraseSelectate.size() < 2) {
            mesajLabel.setText("Trebuie sa selectezi cel putin doua orașe.");
            return;
        }

        int N = oraseSelectate.size();
        int[][] matriceDistantelor = new int[N][N];

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                matriceDistantelor[i][j] = calculeazaDistanta(oraseSelectate.get(i), oraseSelectate.get(j));
            }
        }

        muchii.clear();
        RezultatComisVoiajor rezultat;

        if ("backtracking".equals(metoda)) {
            rezultat = backtracking(matriceDistantelor);
        } else {
            rezultat = greedy(matriceDistantelor);
        }

        int[] traseu = rezultat.traseu;
        int distantaTotala = rezultat.distantaTotala;

        for (int i = 0; i < traseu.length - 1; i++) {
            muchii.add(new int[] { traseu[i], traseu[i + 1] });
        }
        muchii.add(new int[] { traseu[traseu.length - 1], traseu[0] });

        mesajLabel.setText("Ruta optima calculata cu " + metoda + ".");
        distantaLabel.setText("Distanta totala: " + distantaTotala + " km.");
        repaint();
    }

    private RezultatComisVoiajor backtracking(int[][] grafic) {
        int N = grafic.length;
        boolean[] vizitat = new boolean[N];
        int[] traseu = new int[N];
        int[] celMaiBunTraseu = new int[N];
        int costMinim = Integer.MAX_VALUE;

        vizitat[0] = true;
        traseu[0] = 0;

        costMinim = backtrack(grafic, 0, 1, 0, traseu, vizitat, celMaiBunTraseu, costMinim);


        int distantaTotalaKm = (int) (costMinim * pixelLaKm);
        return new RezultatComisVoiajor(celMaiBunTraseu, distantaTotalaKm);
    }

    private int backtrack(int[][] grafic, int poz, int numar, int cost, int[] traseu, boolean[] vizitat, int[] celMaiBunTraseu, int costMinim) {
        int N = grafic.length;

        if (numar == N && grafic[poz][0] > 0) {
            int costTotal = cost + grafic[poz][0];
            if (costTotal < costMinim) {
                System.arraycopy(traseu, 0, celMaiBunTraseu, 0, N);
                costMinim = costTotal;
            }
            return costMinim;
        }

        for (int i = 1; i < N; i++) {
            if (!vizitat[i] && grafic[poz][i] > 0) {
                vizitat[i] = true;
                traseu[numar] = i;
                costMinim = backtrack(grafic, i, numar + 1, cost + grafic[poz][i], traseu, vizitat, celMaiBunTraseu, costMinim);
                vizitat[i] = false;
            }
        }

        return costMinim;
    }

    private RezultatComisVoiajor greedy(int[][] grafic) {
        int N = grafic.length;
        boolean[] vizitat = new boolean[N];
        int[] traseu = new int[N];
        int orasCurent = 0;
        int costTotal = 0;

        vizitat[orasCurent] = true;
        traseu[0] = orasCurent;

        for (int i = 1; i < N; i++) {
            int orasUrmator = -1;
            int costMinim = Integer.MAX_VALUE;

            for (int j = 0; j < N; j++) {
                if (!vizitat[j] && grafic[orasCurent][j] < costMinim && grafic[orasCurent][j] > 0) {
                    orasUrmator = j;
                    costMinim = grafic[orasCurent][j];
                }
            }

            vizitat[orasUrmator] = true;
            traseu[i] = orasUrmator;
            costTotal += costMinim;
            orasCurent = orasUrmator;
        }

        costTotal += grafic[orasCurent][0];


        int distantaTotalaKm = (int) (costTotal * pixelLaKm);
        return new RezultatComisVoiajor(traseu, distantaTotalaKm);
    }

    private int calculeazaDistanta(Oras oras1, Oras oras2) {

        int distantaInPixeli = (int) Math.sqrt(Math.pow(oras1.x - oras2.x, 2) + Math.pow(oras1.y - oras2.y, 2));

        return (int) (distantaInPixeli * pixelLaKm);
    }


    private double calculeazaPixelLaKm() {
        int distantaRealKm = 324;

        Oras bucuresti = orase.get(0);
        Oras cluj = orase.get(1);


        int distantaInPixeli = (int) Math.sqrt(Math.pow(bucuresti.x - cluj.x, 2) + Math.pow(bucuresti.y - cluj.y, 2));


        return (double) distantaRealKm / distantaInPixeli;
    }

}
