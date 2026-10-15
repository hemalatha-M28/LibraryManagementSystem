import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.HashMap;

public class LibrarySystem extends JFrame
{
    private JTextField txtBookId, txtTitle, txtAuthor;
    private JComboBox<String> cmbStatus;
    private JButton btnLoad, btnSearch, btnAdd;
    private JTable bookTable;
    private DefaultTableModel tableModel;

    private HashMap<String, Book> bookMap = new HashMap<>();

    public LibrarySystem()
    {
        DatabaseHelper.initializeDatabase();

        setTitle("Library Book Management System");
        setSize(750, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        inputPanel.add(new JLabel("Book ID:"));
        txtBookId = new JTextField();
        inputPanel.add(txtBookId);

        inputPanel.add(new JLabel("Book Title:"));
        txtTitle = new JTextField();
        inputPanel.add(txtTitle);

        inputPanel.add(new JLabel("Author Name:"));
        txtAuthor = new JTextField();
        inputPanel.add(txtAuthor);

        inputPanel.add(new JLabel("Status:"));

        String[] statuses = {"Available", "Issued", "Reserved", "Lost"};
        cmbStatus = new JComboBox<>(statuses);
        inputPanel.add(cmbStatus);

        JPanel buttonPanel = new JPanel(new FlowLayout());

        btnLoad = new JButton("Load Books");
        btnSearch = new JButton("Search Book");
        btnAdd = new JButton("Add Book");

        buttonPanel.add(btnLoad);
        buttonPanel.add(btnSearch);
        buttonPanel.add(btnAdd);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(inputPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);

        String[] columns = {"Book ID", "Book Title", "Author Name", "Status"};

        tableModel = new DefaultTableModel(columns, 0);
        bookTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(bookTable);

        add(scrollPane, BorderLayout.CENTER);

        wireUpEvents();

        loadBooksFromDatabase();

        setVisible(true);
    }

    private void wireUpEvents()
    {
        btnLoad.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                loadBooksFromDatabase();
            }
        });

        btnSearch.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                String id = txtBookId.getText().trim();

                if(id.isEmpty())
                {
                    JOptionPane.showMessageDialog(
                        LibrarySystem.this,
                        "Please enter a Book ID to search."
                    );
                    return;
                }

                Book b = bookMap.get(id);

                if(b != null)
                {
                    txtTitle.setText(b.getTitle());
                    txtAuthor.setText(b.getAuthor());
                    cmbStatus.setSelectedItem(b.getStatus());
                }
                else
                {
                    JOptionPane.showMessageDialog(
                        LibrarySystem.this,
                        "Book ID not found!"
                    );
                }
            }
        });

        btnAdd.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                String id = txtBookId.getText().trim();
                String title = txtTitle.getText().trim();
                String author = txtAuthor.getText().trim();
                String status = (String) cmbStatus.getSelectedItem();

                if(id.isEmpty() || title.isEmpty() || author.isEmpty())
                {
                    JOptionPane.showMessageDialog(
                        LibrarySystem.this,
                        "All fields are required!"
                    );
                    return;
                }

                if(bookMap.containsKey(id))
                {
                    JOptionPane.showMessageDialog(
                        LibrarySystem.this,
                        "Error: Book ID already exists!"
                    );
                    return;
                }

                String sql = "INSERT INTO book VALUES (?, ?, ?, ?)";

                try(
                    Connection conn = DatabaseHelper.getConnection();
                    PreparedStatement ps = conn.prepareStatement(sql)
                )
                {
                    ps.setString(1, id);
                    ps.setString(2, title);
                    ps.setString(3, author);
                    ps.setString(4, status);

                    ps.executeUpdate();

                    Book b = new Book(id, title, author, status);

                    bookMap.put(id, b);

                    tableModel.addRow(new Object[]{
                        id, title, author, status
                    });

                    txtBookId.setText("");
                    txtTitle.setText("");
                    txtAuthor.setText("");
                    cmbStatus.setSelectedIndex(0);

                    JOptionPane.showMessageDialog(
                        LibrarySystem.this,
                        "Book added successfully!"
                    );
                }
                catch(Exception ex)
                {
                    JOptionPane.showMessageDialog(
                        LibrarySystem.this,
                        "Database Error: " + ex.getMessage()
                    );
                }
            }
        });
    }

    private void loadBooksFromDatabase()
    {
        tableModel.setRowCount(0);
        bookMap.clear();

        String sql = "SELECT * FROM book";

        try(
            Connection conn = DatabaseHelper.getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql)
        )
        {
            while(rs.next())
            {
                String id = rs.getString("book_id");
                String title = rs.getString("book_title");
                String author = rs.getString("author_name");
                String status = rs.getString("status");

                Book b = new Book(id, title, author, status);

                bookMap.put(id, b);

                tableModel.addRow(new Object[]{
                    id, title, author, status
                });
            }
        }
        catch(Exception ex)
        {
            JOptionPane.showMessageDialog(
                this,
                "Failed to load books: " + ex.getMessage()
            );
        }
    }

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(() -> new LibrarySystem());
    }
}