import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.HashMap;

public class LibraryApp extends JFrame
{
    JTextField idField, titleField, authorField;
    JComboBox<String> statusBox;
    JButton loadButton, searchButton, addButton;
    JTable table;
    DefaultTableModel model;

    HashMap<String, Book> books = new HashMap<>();

    Connection con;

    LibraryApp()
    {
        setTitle("Library Management System");
        setSize(750, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        try
        {
            con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/librarydb",
                "root",
                "YOUR_MYSQL_PASSWORD"
            );
        }
        catch(Exception e)
        {
            JOptionPane.showMessageDialog(this, e.getMessage());
            return;
        }

        JPanel p = new JPanel(new GridLayout(4, 2, 5, 5));

        p.add(new JLabel("Book ID"));
        idField = new JTextField();
        p.add(idField);

        p.add(new JLabel("Book Title"));
        titleField = new JTextField();
        p.add(titleField);

        p.add(new JLabel("Author Name"));
        authorField = new JTextField();
        p.add(authorField);

        p.add(new JLabel("Status"));
        String status[] = {"Available", "Issued", "Reserved", "Lost"};
        statusBox = new JComboBox<>(status);
        p.add(statusBox);

        add(p, BorderLayout.NORTH);

        JPanel bp = new JPanel();

        loadButton = new JButton("Load Books");
        searchButton = new JButton("Search Book");
        addButton = new JButton("Add Book");

        bp.add(loadButton);
        bp.add(searchButton);
        bp.add(addButton);

        add(bp, BorderLayout.CENTER);

        model = new DefaultTableModel(
            new String[]{"Book ID", "Book Title", "Author Name", "Status"}, 0
        );

        table = new JTable(model);

        add(new JScrollPane(table), BorderLayout.SOUTH);

        loadButton.addActionListener(e -> loadBooks());
        searchButton.addActionListener(e -> searchBook());
        addButton.addActionListener(e -> addBook());

        loadBooks();

        setVisible(true);
    }

    void loadBooks()
    {
        try
        {
            books.clear();
            model.setRowCount(0);

            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM book");

            while(rs.next())
            {
                String id = rs.getString("book_id");
                String title = rs.getString("book_title");
                String author = rs.getString("author_name");
                String status = rs.getString("status");

                Book b = new Book(id, title, author, status);

                books.put(id, b);

                model.addRow(new Object[]{
                    b.book_id, b.book_title, b.author_name, b.status
                });
            }

            rs.close();
            st.close();
        }
        catch(Exception e)
        {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    void searchBook()
    {
        String id = idField.getText().trim();

        Book b = books.get(id);

        if(b != null)
        {
            titleField.setText(b.book_title);
            authorField.setText(b.author_name);
            statusBox.setSelectedItem(b.status);
        }
        else
        {
            JOptionPane.showMessageDialog(this, "Book not found");
        }
    }

    void addBook()
    {
        String id = idField.getText().trim();
        String title = titleField.getText().trim();
        String author = authorField.getText().trim();
        String status = statusBox.getSelectedItem().toString();

        if(id.isEmpty() || title.isEmpty() || author.isEmpty())
        {
            JOptionPane.showMessageDialog(this, "Please fill all fields");
            return;
        }

        if(books.containsKey(id))
        {
            JOptionPane.showMessageDialog(this, "Book ID already exists");
            return;
        }

        try
        {
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO book VALUES (?, ?, ?, ?)"
            );

            ps.setString(1, id);
            ps.setString(2, title);
            ps.setString(3, author);
            ps.setString(4, status);

            ps.executeUpdate();
            ps.close();

            Book b = new Book(id, title, author, status);

            books.put(id, b);

            model.addRow(new Object[]{
                b.book_id, b.book_title, b.author_name, b.status
            });

            JOptionPane.showMessageDialog(this, "Book added successfully");

            idField.setText("");
            titleField.setText("");
            authorField.setText("");
            statusBox.setSelectedIndex(0);
        }
        catch(Exception e)
        {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    public static void main(String args[])
    {
        SwingUtilities.invokeLater(() -> new LibraryApp());
    }
}
