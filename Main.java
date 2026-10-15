import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Main extends JFrame {

    private final ArrayList<Vara> varukorg = new ArrayList<>();
    private final ArrayList<Vara> sortiment = new ArrayList<>();

    private final JTextArea kvitto = new JTextArea();
    private final JTextField namnFalt = new JTextField(12);
    private final JTextField antalFalt = new JTextField("1", 4);
    private final JLabel totalText = new JLabel("Totalt: 0.00 kr");

    public Main(){
        setTitle("Kassasystem");
        setSize(950,600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel vanster = new JPanel(new BorderLayout());
        vanster.setBackground(new Color(80, 230, 0));

        JPanel knappar = new JPanel(new FlowLayout(FlowLayout.LEFT));

        String[] kategorier = {
                "Alla", "Frukt", "Dryck", "Mejeri", "Godis"
        };

        for (String kategori : kategorier) {
            JButton knapp = new JButton(kategori);
            knapp.addActionListener(e -> visaVaror(kategori));
            knappar.add(knapp);
        }

        JButton rensa = new JButton("Rensa");
        rensa.addActionListener(e -> {
            varukorg.clear();
            uppdateraKvitto();
        });
        knappar.add(rensa);

        vanster.add(knappar, BorderLayout.NORTH);

        JPanel varuPanel = new JPanel(new GridLayout(0, 3, 10, 10));
        varuPanel.setBackground(new Color(80, 230, 0));
        varuPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JScrollPane varuScroll = new JScrollPane(varuPanel);
        vanster.add(varuScroll, BorderLayout.CENTER);

        JPanel nederdel = new JPanel(new FlowLayout());
        nederdel.setBackground(Color.DARK_GRAY);

        nederdel.add(new JLabel("Vara:"));
        nederdel.add(namnFalt);
        nederdel.add(new JLabel("Antal:"));
        nederdel.add(antalFalt);

        JButton laggTill = new JButton("Lägg till");
        laggTill.addActionListener(e -> laggTillMedNamn());
        nederdel.add(laggTill);

        JButton betala = new JButton("Betala");
        betala.addActionListener(e -> betala());
        nederdel.add(betala);

        vanster.add(nederdel, BorderLayout.SOUTH);

        JPanel hoger = new JPanel(new BorderLayout());
        hoger.setPreferredSize(new Dimension(330, 600));

        kvitto.setEditable(false);
        kvitto.setFont(new Font("Monospaced", Font.PLAIN, 13));
        hoger.add(new JScrollPane(kvitto), BorderLayout.CENTER);

        totalText.setFont(new Font("Arial", Font.BOLD, 20));
        hoger.add(totalText, BorderLayout.SOUTH);

        add(vanster, BorderLayout.CENTER);
        add(hoger, BorderLayout.EAST);

        visaVaror("Alla");
        uppdateraKvitto();

        setVisible(true);
    }

    private void skapaSortiment() {
        sortiment.add(new Vara("Äpple", 10, "Frukt"));
        sortiment.add(new Vara("Banan", 8, "Frukt"));
        sortiment.add(new Vara("Apelsin", 12, "Frukt"));
        sortiment.add(new Vara("Cola", 20, "Dryck"));
        sortiment.add(new Vara("Vatten", 15, "Dryck"));
        sortiment.add(new Vara("Mjölk", 18, "Mejeri"));
        sortiment.add(new Vara("Ost", 35, "Mejeri"));
        sortiment.add(new Vara("Choklad", 25, "Godis"));
        sortiment.add(new Vara("Godispåse", 30, "Godis"));
    }

    private JPanel varuPanel;

    private void visaVaror(String kategori) {
        if (varuPanel == null) {
            varuPanel = (JPanel)
                    ((JScrollPane)((JPanel)getContentPane()
                            .getComponent(0)).getComponent(1))
                            .getViewport().getView();
        }

        varuPanel.removeAll();

        for (Vara vara : sortiment) {
            if (kategori.equals("Alla") ||
                    vara.kategori.equals(kategori)) {

                JButton knapp = new JButton(
                        "<html>" + vara.namn + "<br>" +
                                vara.pris + " kr</html>"
                );

                knapp.addActionListener(e -> laggTillVara(vara));
                varuPanel.add(knapp);
            }
        }

        varuPanel.revalidate();
        varuPanel.repaint();
    }

    private void laggTillVara(Vara vara) {
        int antal;

        try {
            antal = Integer.parseInt(antalFalt.getText().trim());

            if (antal <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                    this, "Ange ett giltigt antal större än 0."
            );
            return;
        }

        for (int i = 0; i < antal; i++) {
            varukorg.add(vara);
        }

        uppdateraKvitto();
    }

    private void laggTillMedNamn() {
        String namn = namnFalt.getText().trim();

        for (Vara vara : sortiment) {
            if (vara.namn.equalsIgnoreCase(namn)) {
                laggTillVara(vara);
                namnFalt.setText("");
                return;
            }
        }

        JOptionPane.showMessageDialog(
                this, "Varan finns inte i sortimentet."
        );
    }

    private double beraknaTotal() {
        double total = 0;

        for (Vara vara : varukorg) {
            total += vara.pris;
        }

        return total;
    }

    private void uppdateraKvitto() {
        StringBuilder text = new StringBuilder();
        text.append("       MITT KASSASYSTEM\n");
        text.append("-----------------------------\n");

        for (Vara vara : varukorg) {
            text.append(String.format(
                    "%-18s %6.2f kr%n",
                    vara.namn, vara.pris
            ));
        }

        text.append("-----------------------------\n");
        text.append(String.format(
                "TOTALT: %.2f kr%n", beraknaTotal()
        ));

        kvitto.setText(text.toString());

        totalText.setText(
                String.format("Totalt: %.2f kr", beraknaTotal())
        );
    }

    private void betala() {
        if (varukorg.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this, "Varukorgen är tom."
            );
            return;
        }

        double total = beraknaTotal();

        JOptionPane.showMessageDialog(
                this,
                String.format(
                        "Betalning genomförd!%nTotalt: %.2f kr",
                        total
                )
        );

        varukorg.clear();
        uppdateraKvitto();
    }

    static class Vara {
        String namn;
        double pris;
        String kategori;

        Vara(String namn, double pris, String kategori) {
            this.namn = namn;
            this.pris = pris;
            this.kategori = kategori;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}

