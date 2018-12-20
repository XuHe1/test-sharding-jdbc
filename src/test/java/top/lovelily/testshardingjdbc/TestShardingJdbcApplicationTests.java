package top.lovelily.testshardingjdbc;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestShardingJdbcApplicationTests {
	@Autowired
	private DataSource dataSource;

	@Test
	public void contextLoads() {
	}

	@Test
	public void testJdbc() throws SQLException {
		Connection connection = dataSource.getConnection();
		String sql = "INSERT INTO `t_order` (`order_id`, `user_id`, `product`, `total_price`)\n" +
				"VALUES\n" +
				"\t(?, ?, ?, ?)";
		PreparedStatement preparedStatement = connection.prepareStatement(sql);
		preparedStatement.setInt(1, 2);
		preparedStatement.setInt(2, 2 );
		preparedStatement.setString(3, "iphoneX");
		preparedStatement.setInt(4, 8000);
		int result = preparedStatement.executeUpdate();
		connection.commit();
		preparedStatement.close();
		connection.close();
		assert(result > 0);


	}

}

