package transactions.mapper;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserDao {
	private final JdbcTemplate jdbcTemplate;

	public UserDao(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public void save(String test1,Integer test2) {
		jdbcTemplate.update("insert into huangfu1(test1,test2) values ('皇甫',100);");
	}
}
