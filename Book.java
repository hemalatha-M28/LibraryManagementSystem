public class Book
{
    String book_id;
    String book_title;
    String author_name;
    String status;

    public Book(String book_id, String book_title, String author_name, String status)
    {
        this.book_id = book_id;
        this.book_title = book_title;
        this.author_name = author_name;
        this.status = status;
    }

    public String getTitle()
    {
        return book_title;
    }

    public String getAuthor()
    {
        return author_name;
    }

    public String getStatus()
    {
        return status;
    }
}