if [ "$USER" = "rmdaba" ] ; then
	docker exec fixme-mysql mysql --host=localhost --user=root --password="Rootroot3" \
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
	-e "ALTER TABLE brokers MODIFY br_id bigint(20) NOT NULL AUTO_INCREMENT;" \
	-e "CREATE TABLE fixme.instruments ( \
		inst_id INT NOT NULL AUTO_INCREMENT , \
		inst_name VARCHAR(255) NOT NULL , \
		inst_amount FLOAT NOT NULL COMMENT 'per unit measure' , \
		inst_unit VARCHAR(25) NOT NULL , \
		inst_ma_id INT NOT NULL , \
		inst_price FLOAT NOT NULL DEFAULT '0.00' , \
		PRIMARY KEY (inst_id)) ENGINE = InnoDB \
		COMMENT = 'inst is the the prefix for instruments table';" \
	-e "CREATE TABLE fixme.markets ( \
		ma_id INT NOT NULL AUTO_INCREMENT , \
		ma_name VARCHAR(255) NOT NULL , \
		ma_username VARCHAR(255) NOT NULL , \
		ma_password VARCHAR(255) NOT NULL , \
		ma_deleted TINYINT(1) NOT NULL DEFAULT '0' , \
		PRIMARY KEY  (ma_id)) ENGINE = InnoDB;"
	# -e "ALTER TABLE brokers AUTO_INCREMENT=100000;"
else
	mysql --host=localhost --user=root --password="Rootroot3" \
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
	-e "ALTER TABLE brokers MODIFY br_id bigint(20) NOT NULL AUTO_INCREMENT;" \
	-e "CREATE TABLE fixme.instruments ( \
		inst_id INT NOT NULL AUTO_INCREMENT , \
		inst_name VARCHAR(255) NOT NULL , \
		inst_amount FLOAT NOT NULL COMMENT 'per unit measure' , \
		inst_unit VARCHAR(25) NOT NULL , \
		inst_ma_id INT NOT NULL , \
		inst_price FLOAT NOT NULL DEFAULT '0.00' , \
		PRIMARY KEY (inst_id)) ENGINE = InnoDB \
		COMMENT = 'inst is the the prefix for instruments table';" \
	-e "CREATE TABLE fixme.markets ( \
		ma_id INT NOT NULL AUTO_INCREMENT , \
		ma_name VARCHAR(255) NOT NULL , \
		ma_username VARCHAR(255) NOT NULL , \
		ma_password VARCHAR(255) NOT NULL , \
		ma_deleted TINYINT(1) NOT NULL DEFAULT '0' , \
		PRIMARY KEY  (ma_id)) ENGINE = InnoDB;"
	# -e "ALTER TABLE brokers AUTO_INCREMENT=100000;"
fi
