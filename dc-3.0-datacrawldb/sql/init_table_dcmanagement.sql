---DON'T MODIFY ME
	DROP TABLE if exists t_site;
	DROP TABLE if exists t_batch;
	DROP TABLE if exists t_subtask;
	DROP TABLE if exists t_task;
	DROP TABLE if exists t_agent_info;
	DROP TABLE if exists t_user;
	DROP TABLE if exists t_dict;
	DROP TABLE if exists t_configuration;
	DROP TABLE if exists t_task_due_message_user;
	DROP TABLE if exists t_site_login_account;
	DROP TABLE if exists t_group;
	CREATE TABLE t_site (
		name varchar(50) NOT NULL,
		description varchar(500),
		domain varchar(50) NOT NULL,
		url varchar(500) NOT NULL,
		info_status integer NOT NULL default 0,
		max_instance integer NOT NULL default 4,
		process_instance integer NOT NULL default 0,
		bdd_id bigint NOT NULL,
		type varchar(50) NOT NULL,
		qa_id bigint NOT NULL,
		qa varchar(50) NOT NULL,
		rw varchar(50) NOT NULL,
		rw_id bigint NOT NULL,
		group_id bigint NOT NULL default 0,
		
		id bigint PRIMARY KEY AUTO_INCREMENT
	);
	create index Index_type on t_site(type);
			create index Index_QA_ID on t_site(qa_id);
			create index Index_RW_ID on t_site(rw_id);
			create index Index_Group_ID on t_site(group_id);
	CREATE TABLE t_batch (
		batch_name varchar(10) NOT NULL,
		task_id bigint NOT NULL,
		split_count integer NOT NULL,
		split_time datetime NOT NULL,
		
		id bigint PRIMARY KEY AUTO_INCREMENT
	);
	create index Index_task_id on t_batch(task_id);
			create index Index_split_time on t_batch(split_time);
	
	CREATE TABLE t_subtask (
		agent_id bigint,
		agent_lan_ipv4 varchar(15),
		agent_wan_ipv4 varchar(15),
		task_id bigint NOT NULL,
		subtask_key varchar(255) NOT NULL,
		script_file varchar(100) NOT NULL,
		script_main varchar(300) NOT NULL,
		scrape_count integer NOT NULL,
		status tinyint NOT NULL default 0,
		group_id bigint NOT NULL default 0,
		site_id integer NOT NULL default 0,
		error_code integer default 0,
		error_msg longtext,
		error_url varchar(500),
		error_sent_flag tinyint NOT NULL default 0,
		sort integer NOT NULL,
		bdd_id bigint NOT NULL,
		timeout bigint NOT NULL,
		batch_id bigint,
		create_day_id bigint NOT NULL,
		create_time datetime NOT NULL,
		start_time datetime,
		last_time datetime,
		
		id bigint PRIMARY KEY AUTO_INCREMENT
	);
	CREATE INDEX Index_hostname_status ON t_subtask (agent_lan_ipv4, status);
			CREATE INDEX Index_taskId_status ON t_subtask (task_id, status);
			create index Index_status on t_subtask(status,last_time);
			create index Index_create_time on t_subtask(create_time);
			create index Index_start_time on t_subtask(start_time);
			create index Index_last_time on t_subtask(last_time);
			create index Index_sort on t_subtask(sort);
			create index Index_group_id on t_subtask(group_id);
			create index Index_scrape_count on t_subtask(scrape_count);
	CREATE TABLE t_task (
		site_id bigint,
		name varchar(50) NOT NULL,
		description varchar(400) NOT NULL,
		split_type tinyint NOT NULL,
		split_file varchar(100) NOT NULL,
		split_main varchar(300) NOT NULL,
		script_file varchar(100) NOT NULL,
		due_time datetime NOT NULL,
		due_check_flag boolean NOT NULL,
		alarm_before_due integer NOT NULL,
		timeout bigint NOT NULL,
		last_split_status integer NOT NULL default 0,
		last_split_msg varchar(500),
		is_enable tinyint NOT NULL default 0,
		is_send_enable tinyint NOT NULL default 1,
		split_wait_time bigint NOT NULL default 0,
		priority integer NOT NULL default 0,
		start_time datetime,
		is_enable_start_time integer default 0,
		last_split_time datetime,
		last_split_key varchar(10),
		last_split_count integer default 0,
		group_id bigint NOT NULL default 0,
		bdd_id bigint NOT NULL,
		
		id bigint PRIMARY KEY AUTO_INCREMENT
	);
	create index Index_siteId on t_task(site_id);
			create index Index_lastSplitTime on t_task(due_time);
			create index Index_dueTime on t_task(due_time);
			create index Index_is_enable on t_task(is_enable);
	CREATE TABLE t_agent_info (
		lan_ipv4 varchar(15),
		wan_ipv4 varchar(15),
		port integer,
		max_process_num integer,
		process_num integer,
		waiting_dts_file_count integer,
		is_enable tinyint,
		is_available tinyint,
		group_id bigint default 0,
		register_time datetime,
		last_access_time datetime,
		last_dts_time datetime,
		
		id bigint PRIMARY KEY AUTO_INCREMENT
	);
	create index Index_ipaddress on t_agent_info(lan_ipv4);
			create index Index_enableFlag on t_agent_info(is_enable);
			create index Index_availableFlag on t_agent_info(is_available);
			create index Index_groupId on t_agent_info(group_id);
	CREATE TABLE t_user (
		name varchar(20),
		email varchar(100),
		
		id bigint PRIMARY KEY AUTO_INCREMENT
	);
	
	CREATE TABLE t_dict (
		type varchar(20),
		value varchar(50),
		text varchar(50),
		
		id bigint PRIMARY KEY AUTO_INCREMENT
	);
	create index Index_1 on t_dict(type,value);
			insert into t_dict (type, value, text) VALUES ('Site.Type', 'Site.Type.SERA', 'SERA');
			insert into t_dict (type, value, text) VALUES ('Site.Type', 'Site.Type.FIX_FORUM', 'FIX_FORUM');
			insert into t_dict (type, value, text) VALUES ('Site.Type.SERA', 'SERA', 'SERA');
			insert into t_dict (type, value, text) VALUES ('Site.Type.FIX_FORUM', 'THREAD', 'Thread');
			insert into t_dict (type, value, text) VALUES ('Site.Type.FIX_FORUM', 'POST', 'Post');
			insert into t_dict (type, value, text) VALUES ('Execute.Cycle', '43200000', '一天2次');
			insert into t_dict (type, value, text) VALUES ('Execute.Cycle', '21600000', '一天4次');
			insert into t_dict (type, value, text) VALUES ('Execute.Cycle', '86400000', '一天1次');
			insert into t_dict (type, value, text) VALUES ('Execute.Cycle', '3600000', '每小时一次');
			insert into t_dict (type, value, text) VALUES ('Script.Path.Default', 'THREAD', 'splitThread.js');
			insert into t_dict (type, value, text) VALUES ('BOOL', '0', '否');
			insert into t_dict (type, value, text) VALUES ('BOOL', '1', '是');
			insert into t_dict (type, value, text) VALUES ('Script.Path.Default', 'POST', 'splitPost.js');
			insert into t_dict (type, value, text) VALUES ('Script.Path.Default', 'SERA', 'splitSera.js');
	CREATE TABLE t_configuration (
		name varchar(20),
		value varchar(50),
		
		id bigint PRIMARY KEY AUTO_INCREMENT
	);
	create index Index_1 on t_configuration (name,value);
			INSERT INTO t_configuration (name, value) VALUES ('DEFAULT_TASK_NUM', 4);
	CREATE TABLE t_task_due_message_user (
		task_id bigint,
		alarm_email varchar(50),
		
		id bigint PRIMARY KEY AUTO_INCREMENT
	);
	create index Index_1 on t_task_due_message_user(task_id);
	CREATE TABLE t_site_login_account (
		site_id bigint,
		account varchar(50),
		password varchar(20),
		last_get_time datetime,
		last_get_key varchar(10),
		is_invalid tinyint default 0,
		
		id bigint PRIMARY KEY AUTO_INCREMENT
	);
	create index Index_ACCOUNT on t_site_login_account(account);
			create index Index_site_id_invalid on t_site_login_account(site_id, is_invalid);
	CREATE TABLE t_group (
		group_name varchar(30),
		site_count integer default 0,
		agent_count integer default 0,
		
		id bigint PRIMARY KEY AUTO_INCREMENT
	);
	INSERT INTO t_group (group_name) values ('默认分组');
		update t_group set id = 1;
