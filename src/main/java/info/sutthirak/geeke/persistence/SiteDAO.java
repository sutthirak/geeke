package info.sutthirak.geeke.persistence;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

public interface SiteDAO {

    @SqlUpdate("insert into site(name, link) values (:name, :link)")
    @Mapper(Site.SiteMapper.class)
    void insert(@Bind("name") String name, @Bind("link") String link);

    @SqlQuery("select count(id) from site where link = :link")
    int findByLink(@Bind("link") String link);
}
