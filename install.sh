if [ "$USER" = "rmdaba" ] ; then
	docker exec swingy-mysql mysql --host=localhost --user=root --password="Rootroot3" \
	-e "CREATE DATABASE IF NOT EXISTS fixme;" -e "USE fixme;" \
	-e "CREATE TABLE brokers ( \
		br_id bigint(20) NOT NULL, \
		br_name varchar(255) NOT NULL, \
		br_username varchar(255) NOT NULL, \
		br_password varchar(255) NOT NULL, \
		br_deleted tinyint(1) NOT NULL DEFAULT '0', \
		br_ip varchar(255) NOT NULL \
		) ENGINE=InnoDB DEFAULT CHARSET=utf8;" \
	-e "ALTER TABLE brokers ADD PRIMARY KEY (br_id);" \
	-e "ALTER TABLE brokers MODIFY br_id bigint(20) NOT NULL AUTO_INCREMENT;"
else
	mysql --host=localhost --user=root --password="Rootroot3" \
	-e "CREATE DATABASE IF NOT EXISTS Swingy;" -e "USE Swingy;" \
	-e "CREATE TABLE brokers ( \
		br_id bigint(20) NOT NULL, \
		br_name varchar(255) NOT NULL, \
		br_username varchar(255) NOT NULL, \
		br_password varchar(255) NOT NULL, \
		br_deleted tinyint(1) NOT NULL DEFAULT '0', \
		br_ip varchar(255) NOT NULL \
	) ENGINE=InnoDB DEFAULT CHARSET=utf8;" \
	-e "ALTER TABLE brokers ADD PRIMARY KEY (br_id);" \
	-e "ALTER TABLE brokers MODIFY br_id bigint(20) NOT NULL AUTO_INCREMENT;"
fi
