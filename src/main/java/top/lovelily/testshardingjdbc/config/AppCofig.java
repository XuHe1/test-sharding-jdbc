package top.lovelily.testshardingjdbc.config;

import com.zaxxer.hikari.HikariDataSource;
import io.shardingjdbc.core.api.ShardingDataSourceFactory;
import io.shardingjdbc.core.api.config.ShardingRuleConfiguration;
import io.shardingjdbc.core.api.config.TableRuleConfiguration;
import io.shardingjdbc.core.api.config.strategy.InlineShardingStrategyConfiguration;
import io.shardingjdbc.core.api.config.strategy.StandardShardingStrategyConfiguration;
import io.shardingjdbc.core.util.DataSourceUtil;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Desc: AppCofig
 * Author: xuhe
 * Date: 2018/12/20 3:04 PM
 * Version: 1.0
 */
@Configuration
public class AppCofig {
    @Bean
    @ConfigurationProperties(prefix = "dataSource1")
    public DataSource dataSource1(){
        return new HikariDataSource();
    }

    @Bean
    @ConfigurationProperties(prefix = "dataSource2")
    public DataSource dataSource2(){
        return new HikariDataSource();
    }

    Map<String, DataSource> createDataSourceMap() {
        Map<String, DataSource> result = new HashMap<>();
        result.put("kyx_order0", dataSource1());
        result.put("kyx_order1", dataSource2());
        return result;
    }

    TableRuleConfiguration getOrderTableRuleConfiguration() {
        TableRuleConfiguration result = new TableRuleConfiguration();
        result.setLogicTable("t_order");
        result.setActualDataNodes("kyx_order${0..1}.t_order${0..1}");
        result.setKeyGeneratorColumnName("order_id");
        return result;
    }


    @Bean(destroyMethod = "close")
    public DataSource dataSource() throws SQLException {
        ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
        shardingRuleConfig.getTableRuleConfigs().add(getOrderTableRuleConfiguration());
        // shardingRuleConfig.getBindingTableGroups().add("t_order");

        shardingRuleConfig.setDefaultDatabaseShardingStrategyConfig(new InlineShardingStrategyConfiguration("user_id", "kyx_order${user_id % 2}"));
        shardingRuleConfig.setDefaultTableShardingStrategyConfig(new InlineShardingStrategyConfiguration("order_id", "t_order${order_id % 2}"));
        DataSource dataSource = ShardingDataSourceFactory.createDataSource(createDataSourceMap(), shardingRuleConfig, new ConcurrentHashMap<>(), new Properties());
        return dataSource;
    }


}
