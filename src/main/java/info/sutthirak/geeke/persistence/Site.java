package info.sutthirak.geeke.persistence;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Site {

    private Long id;
    private String name;
    private String link;

    public Site(Long id,String name,String link){
        this.id = id;
        this.name = name;
        this.link = link;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public class SiteMapper implements ResultSetMapper<Site>
    {
        public Site map(int index, ResultSet r, StatementContext ctx) throws SQLException
        {
            return new Site(r.getLong("id"),r.getString("site"),r.getString("link"));
        }
    }

}
