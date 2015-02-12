package cats.mail.client.mail;

import cats.mail.client.CMailClient;
import java.util.Date;
import java.util.function.Predicate;

public class Mail implements Comparable<Mail>, Cloneable{

    public enum Folder{
        INBOX("Inbox", mail -> !mail.read),
        SENT("Sent", mail -> mail.from.equalsIgnoreCase(CMailClient.email)),
        RECEIVED("Received", mail -> mail.to.equalsIgnoreCase(CMailClient.email));

        public final String name;
        public final Predicate<Mail> condition;

        private Folder(final String name, final Predicate<Mail> condition){
            this.name = name;
            this.condition = condition;
        }

        public String toString(){
            return name;
        }
    }

    public String from;
    public String to;
    public final String subject;
    public final String body;
    public final Date date;
    public final long id;
    public boolean read;

    public Folder folder;

    public Mail(final String from, final String to, final String subject, final String body, final Date date, final long id, final boolean read){
        this.from = from;
        this.to = to;
        this.subject = subject;
        this.body = body;
        this.date = date;
        this.id = id;
        this.read = read;

        for(final Mail.Folder f : Mail.Folder.values()){
            if(f.condition.test(this)){
                folder = f;
                break;
            }
        }
        if(folder == Mail.Folder.INBOX && Mail.Folder.SENT.condition.test(this))
            folder = Mail.Folder.SENT;
    }

    public Mail clone(){
        return new Mail(from, to, subject, body, date, id, read);
    }

    public int compareTo(final Mail mail){
        return date.compareTo(mail.date);
    }

    public boolean equals(final Object o){
        if(o == null)
            return false;
        if(o == this)
            return true;
        return o instanceof Mail && id == ((Mail) o).id;
    }
}
