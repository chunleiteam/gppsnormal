/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2014/11/20 10:44:50                          */
/*==============================================================*/


drop table if exists Activity;

drop table if exists ActivityRef;

drop table if exists Borrower;

drop table if exists BorrowerAccount;

drop table if exists CardBinding;

drop table if exists FinancingRequest;

drop table if exists Govermentorder;

drop table if exists Help;

drop table if exists Lender;

drop table if exists LenderAccount;

drop table if exists Letter;

drop table if exists News;

drop table if exists Notice;

drop table if exists PayBackModel;

drop table if exists Product;

drop table if exists ProductAction;

drop table if exists StateLog;

drop table if exists Submit;

drop table if exists Task;

drop table if exists admin;

drop table if exists adminGroup;

drop table if exists cashstream;

drop table if exists groupmember;

drop table if exists handlelog;

drop table if exists payback;

drop table if exists productseries;

/*==============================================================*/
/* Table: Activity                                              */
/*==============================================================*/
create table Activity
(
   id                   integer not null auto_increment,
   name                 varchar(255) not null default '0' comment '0:public
            1:private',
   applystarttime       bigint not null comment '0:新手问题
            1:常见问题',
   applyendtime         bigint not null default 0 comment '0:lender
            1:borrower',
   starttime            bigint not null,
   question             varchar(2000),
   answer               mediumtext,
   createtime           bigint not null,
   state                int not null default 0 comment '0:未发布
            1：报名
            2：进行
            3：结束',
   url                  varchar(255),
   description            mediumtext,
   accessory            mediumtext,
   primary key (id)
);

/*==============================================================*/
/* Table: ActivityRef                                           */
/*==============================================================*/
create table ActivityRef
(
   id                   integer not null auto_increment,
   participatortype     int not null default 0 comment '0:lender
            1:borrower',
   participatorId       Integer not null,
   ActivityId           Integer not null,
   applytime            bigint not null,
   participate           int not null default 0 comment '0:参加
            1：未参加',
   awarddetail          varchar(255),
   description          varchar(255),
   primary key (id)
);

/*==============================================================*/
/* Table: Borrower                                              */
/*==============================================================*/
create table Borrower
(
   id                   integer not null auto_increment,
   name                 varchar(50),
   tel                  varchar(50),
   email                varchar(50),
   loginId              varchar(255),
   password             varchar(255) not null,
   identitycard         varchar(50),
   accountid            integer not null,
   createtime           BIGINT not null,
   material             mediumtext comment '记录了企业相关资料的附件路径',
   request              mediumtext,
   privilege            int not null comment '10：有查看权限的企业用户
            11：申请融资权限的企业用户
            12：有融资权限的企业用户',
   creditValue          int not null default 0,
   companyName          varchar(255),
   license              varchar(255),
   corporationPhone     varchar(255),
   corporationName      varchar(255),
   corporationAddr      varchar(255),
   thirdPartyAccount    varchar(255),
   level                int not null default 0,
   lastModifyTime       BIGINT not null default 0,
   brange               varchar(255),
   cardBindingId        integer,
   authorizeTypeOpen    int not null default 0,
   accountNumber		varchar(255),
   primary key (id)
);

/*==============================================================*/
/* Table: BorrowerAccount                                       */
/*==============================================================*/
create table BorrowerAccount
(
   id                   integer not null auto_increment,
   total                decimal(12,2) not null default 0,
   freeze               decimal(12,2) not null default 0,
   usable               decimal(12,2) not null default 0,
   primary key (id)
);

/*==============================================================*/
/* Table: CardBinding                                           */
/*==============================================================*/
create table CardBinding
(
   id                   Integer not null auto_increment,
   CardNo               varchar(100) not null,
   CardType             int not null default 0,
   BankCode             varchar(100) not null,
   BranchBankName       varchar(100) not null,
   Province             varchar(100) not null,
   City                 varchar(100) not null,
   primary key (id)
);

/*==============================================================*/
/* Table: FinancingRequest                                      */
/*==============================================================*/
create table FinancingRequest
(
   id                   integer not null auto_increment,
   govermentOrderName   varchar(255),
   govermentOrderDetail varchar(255),
   applyFinancingAmount int not null default 0,
   rate                 varchar(50),
   state                int not null default 0,
   createtime           bigint not null default 0,
   lastModifyTime       bigint not null default 0,
   borrowerID           integer not null,
   primary key (id)
);

/*==============================================================*/
/* Table: Govermentorder                                        */
/*==============================================================*/
create table Govermentorder
(
   ID                   integer not null auto_increment,
   borrowerId           integer not null,
   material             mediumtext comment '记录相关资料附件的路径',
   state                int not null comment '0：申请融资
            -1：驳回重填
            -2：重新申请
            -100：拒绝
            1：审核通过
            2：产品已发布
            3：融资中
            4：还款中
            5：已关闭',
   financingStarttime   bigint not null default 0,
   financingEndtime     bigint not null default 0,
   createtime           bigint not null default 0,
   title                varchar(255),
   incomeStarttime      bigint not null,
   lastModifytime       bigint not null,
   description          varchar(2000),
   FinancingRequestId   integer,
   primary key (ID)
);

/*==============================================================*/
/* Table: Help                                                  */
/*==============================================================*/
create table Help
(
   id                   integer not null auto_increment,
   type                 int not null default 0 comment '0:public
            1:private',
   publictype           int not null comment '0:新手问题
            1:常见问题',
   questionertype       int not null default 0 comment '0:lender
            1:borrower',
   questionerId         Integer,
   question             varchar(2000),
   answer               mediumtext,
   createtime           bigint not null,
   answertime bigint,
   primary key (id)
);

/*==============================================================*/
/* Table: Lender                                                */
/*==============================================================*/
create table Lender
(
   id                   integer not null auto_increment,
   name                 varchar(50),
   tel                  varchar(50),
   email                varchar(50),
   loginId              varchar(255),
   password             varchar(255) not null,
   identitycard         varchar(50),
   accountid            integer not null,
   createtime           BIGINT not null,
   privilege            int not null comment '00：普通用户
            01：vip1',
   grade                int not null default 0,
   level                int not null default 0,
   sex                  int not null default 0,
   address              varchar(255),
   thirdPartyAccount    varchar(255),
   annualIncome         varchar(255),
   cardbindingId        integer,
   accountNumber		varchar(255),
   primary key (id)
);

/*==============================================================*/
/* Table: LenderAccount                                         */
/*==============================================================*/
create table LenderAccount
(
   id                   integer not null auto_increment,
   total                decimal(12,2) not null default 0,
   freeze               decimal(12,2) not null default 0,
   usable               decimal(12,2) not null default 0,
   used                 decimal(12,2) not null default 0,
   totalincome          decimal(12,2) not null default 0,
   expectedincome       decimal(12,2) not null default 0,
   primary key (id)
);

/*==============================================================*/
/* Table: Letter                                                */
/*==============================================================*/
create table Letter
(
   id                   integer not null auto_increment,
   title                varchar(255),
   receivertype         int not null default 0 comment '0:lender
            1:borrower',
   receiverId           Integer not null,
   content              mediumtext,
   createtime           bigint not null,
   markRead             int not null default 0 comment '0:未读
            1：已读',
   readtime bigint,
   primary key (id)
);

/*==============================================================*/
/* Table: News                                                  */
/*==============================================================*/
create table News
(
   id                   integer not null auto_increment,
   title                varchar(255) not null default '0' comment '0:public
            1:private',
   content              mediumtext,
   publishtime          bigint not null,
   publictype int not null default 0,
   primary key (id)
);

/*==============================================================*/
/* Table: Notice                                                */
/*==============================================================*/
create table Notice
(
   id                   integer not null auto_increment,
   title                varchar(255) not null default '0' comment '0:public
            1:private',
   content              mediumtext,
   publishtime          bigint not null,
   usefor               int not null default -1 comment '-1:不限
            0：lender
            1：borrower',
   level                int not null default -1 comment '-1：不限，to不为-1时启用',
   publictype int not null default 0,
   primary key (id)
);

/*==============================================================*/
/* Table: PayBackModel                                          */
/*==============================================================*/
create table PayBackModel
(
   ID                   int not null auto_increment,
   type                 int not null default 2 comment '0： 等额本息
            1：先还利息，到期还本
            2：到期还本付息',
   freqencyType         int not null default 9 comment '0:按日
            1：按周
            2：按半月
            3：按月
            4：按指定日期
            9：未指定',
   primary key (ID)
);

/*==============================================================*/
/* Table: Product                                               */
/*==============================================================*/
create table Product
(
   ID                   integer not null auto_increment,
   state                int not null default 0 comment '0：投标中
            1：还款中
            -1：申请关闭
            -2：延期
            2：已关闭
            3：还款完成
            4：流标',
   GovermentorderId     integer not null,
   expectAmount         decimal(12,2) not null default 0,
   realAmount           decimal(12,2) not null default 0,
   rate                 decimal(3,3) not null default 0,
   paybackmodel         int not null comment '0： 等额本息
            1：先还利息，到期还本
            2：到期还本付息
            
            
            0:按日
            1：按周
            2：按半月
            3：按月
            4：按指定日期
            9：未指定',
   accessory            mediumtext,
   productseriesId      integer not null,
   levelToBuy           int not null,
   createtime           bigint not null default 0,
   minimum              int not null default 1,
   miniAdd              int not null default 1,
   description          varchar(2000),
   incomeEndtime        bigint not null default 0,
   lastmodifytime       bigint not null,
   primary key (ID)
);

/*==============================================================*/
/* Table: ProductAction                                         */
/*==============================================================*/
create table ProductAction
(
   ID                   integer not null auto_increment,
   createtime           bigint not null default 0 comment '0：投标中
            1：还款中
            -1：申请关闭
            2：已关闭
            3：还款完成
            4：流标',
   title                varchar(255) not null,
   detail               varchar(2000) default '0',
   type                 int not null default 0 comment '-2：严重问题
            -1：不顺利
            0：正常
            1：利好',
   productId            integer not null,
   primary key (ID)
);

/*==============================================================*/
/* Table: StateLog                                              */
/*==============================================================*/
create table StateLog
(
   ID                   integer not null auto_increment,
   type                 int not null comment '0：submit
            1：product
            2：Govermentorder',
   createtime           bigint not null,
   source               int not null,
   target               int not null,
   refid                integer not null,
   primary key (ID)
);

/*==============================================================*/
/* Table: Submit                                                */
/*==============================================================*/
create table Submit
(
   Id                   integer not null auto_increment,
   state                int not null default 0 comment '0:申请竞标->
            1:待支付
            	（支付）
            2:竞标中
            	(融资审核成功)
            3:还款中
            4：还款完毕
            
            6:流标(融资审核不成功)
            7:退订（未支付）
            8：异常（额度不足）申请不成功',
   createtime           bigint not null,
   lenderId             integer not null,
   productId            integer not null,
   lastmodifytime       bigint not null,
   amount               decimal(12,2) not null default 0,
   primary key (Id)
);

/*==============================================================*/
/* Table: Task                                                  */
/*==============================================================*/
create table Task
(
   id                   integer not null auto_increment,
   type                 int not null,
   createTime           bigint not null,
   state                int not null default 0,
   productId            integer not null,
   payBackId            integer,
   primary key (id)
);

/*==============================================================*/
/* Table: admin                                                 */
/*==============================================================*/
create table admin
(
   id                   integer not null auto_increment,
   name                 varchar(50),
   tel                  varchar(50),
   email                varchar(50),
   loginId              varchar(255),
   password             varchar(255) not null,
   createtime           BIGINT not null,
   privilege            int not null default 0 comment '0: 只看
            1：可操作',
   primary key (id)
);

/*==============================================================*/
/* Table: adminGroup                                            */
/*==============================================================*/
create table adminGroup
(
   id                   integer not null,
   primary key (id)
);

/*==============================================================*/
/* Table: cashstream                                            */
/*==============================================================*/
create table cashstream
(
   id                   integer not null auto_increment,
   action               int not null comment '0:向账户充值
            1:冻结(包括借款方竞标冻结/融资方还钱冻结)
            2：解冻
            3:借款方汇出给融资方
            4:融资方:汇入借款方
            5:从账户提现',
   chiefamount          decimal(12,2) not null default 0,
   createtime           bigint not null,
   LenderAccountId      integer,
   BorrowerAccountId    integer,
   submitid             integer,
   interest             decimal(12,2) not null default 0,
   description          varchar(2000),
   paybackId            integer,
   state                int not null default 1,
   loanNo               varchar(255),
   fee                  decimal(12,2) not null default 0,
   primary key (id)
);

/*==============================================================*/
/* Table: groupmember                                           */
/*==============================================================*/
create table groupmember
(
   id                   integer not null,
   groupid              integer not null,
   adminid              integer not null,
   verify               integer not null,
   primary key (id)
);

/*==============================================================*/
/* Table: handlelog                                             */
/*==============================================================*/
create table handlelog
(
   id                   integer not null auto_increment,
   handlertype          int not null comment '0:lender
            1:borrower
            2:admin',
   handlerId            integer not null,
   callService          varchar(255),
   callmethod           varchar(255),
   callparam            mediumtext,
   handletime 			bigint default 0 not null,
   primary key (id)
);

/*==============================================================*/
/* Table: payback                                               */
/*==============================================================*/
create table payback
(
   ID                   integer not null auto_increment,
   borroweraccountid    integer not null,
   productId            integer not null,
   state                int not null comment '0：待还款
            1：已还款
            2：延期(待确定)',
   type                 int not null default 0 comment '0:利息和本息;1:一次性本金',
   chiefamount          decimal(12,2) not null,
   deadline             bigint not null,
   interest             decimal(12,2) not null,
   realtime             bigint default 0 not null,
   checktime			bigint default 0 not null,
   checkResult			int not null default 0,
   primary key (ID)
);

/*==============================================================*/
/* Table: productseries                                         */
/*==============================================================*/
create table productseries
(
   ID                   integer not null auto_increment,
   title                varchar(100) not null,
   tag                  mediumtext,
   description          varchar(2000),
   type                 int not null default 0,
   typedetail           varchar(255),
   primary key (ID)
);

alter table ActivityRef add constraint FK_Ref_Activity_ActivityRef foreign key (ActivityId)
      references Activity (id) on delete restrict on update restrict;

alter table Borrower add constraint FK_Ref_Borrower_CardBinding foreign key (cardBindingId)
      references CardBinding (id) on delete restrict on update restrict;

alter table Borrower add constraint FK_Ref_borrower_account foreign key (accountid)
      references BorrowerAccount (id) on delete restrict on update restrict;

alter table FinancingRequest add constraint FK_Reference_FinancingRequest_Borrower foreign key (borrowerID)
      references Borrower (id) on delete restrict on update restrict;

alter table Govermentorder add constraint FK_Ref_Govermentorder_borrower foreign key (borrowerId)
      references Borrower (id) on delete restrict on update restrict;

alter table Govermentorder add constraint FK_Reference_GovermentOrder_FinancingRequest foreign key (FinancingRequestId)
      references FinancingRequest (id) on delete restrict on update restrict;

alter table Lender add constraint FK_Ref_Lender_CardBinding foreign key (cardbindingId)
      references CardBinding (id) on delete restrict on update restrict;

alter table Lender add constraint FK_Ref_lender_account foreign key (accountid)
      references LenderAccount (id) on delete restrict on update restrict;

alter table Product add constraint FK_Ref_Product_Govermentorder foreign key (GovermentorderId)
      references Govermentorder (ID) on delete restrict on update restrict;

alter table Product add constraint FK_Ref_product_series foreign key (productseriesId)
      references productseries (ID) on delete restrict on update restrict;

alter table ProductAction add constraint FK_Ref_ProductAction_Product foreign key (productId)
      references Product (ID) on delete restrict on update restrict;

alter table Submit add constraint FK_Ref_submit_lender foreign key (lenderId)
      references Lender (id) on delete restrict on update restrict;

alter table Submit add constraint FK_Ref_submit_product foreign key (productId)
      references Product (ID) on delete restrict on update restrict;

alter table cashstream add constraint FK_Ref_cashstream_borroweraccount foreign key (BorrowerAccountId)
      references BorrowerAccount (id) on delete restrict on update restrict;

alter table cashstream add constraint FK_Ref_cashstream_lenderaccount foreign key (LenderAccountId)
      references LenderAccount (id) on delete restrict on update restrict;

alter table cashstream add constraint FK_Ref_cashstream_payback foreign key (paybackId)
      references payback (ID) on delete restrict on update restrict;

alter table cashstream add constraint FK_Ref_cashstream_submit foreign key (submitid)
      references Submit (Id) on delete restrict on update restrict;

alter table payback add constraint FK_Ref_payback_borroweraccount foreign key (borroweraccountid)
      references BorrowerAccount (id) on delete restrict on update restrict;

alter table payback add constraint FK_Ref_payback_product foreign key (productId)
      references Product (ID) on delete restrict on update restrict;

      
      
      
/*======================20150107修改========================================*/
alter table Borrower add(contactor mediumtext);
alter table GovermentOrder add(formalName varchar(255));
alter table GovermentOrder add(formalLevel varchar(255));
alter table GovermentOrder add(formalAmount varchar(255));
alter table GovermentOrder add(tenderUnits varchar(255));
alter table GovermentOrder add(formalLink varchar(255));
      
      
      


/*======================20150116修改========================================*/
/*==============================================================*/
/* Table: Invite                                              */
/*==============================================================*/
create table Invite
(
   id                   integer not null auto_increment,
   code                 varchar(255) not null unique,
   attributeTo          integer not null,
   registerBy           integer,
   state                int not null default 1,
   primary key (id)
);

      
      
      
      
 /* 产品线初始化
  * 
  * */
INSERT INTO `productseries` VALUES (1,'稳健型','<?xml version=\"1.0\" encoding=\"UTF-8\"?><TagCol><items><Item description=\"较低风险\" title=\"较低风险\"/><Item description=\"收益稳定\" title=\"收益稳定\"/></items></TagCol>','本类型产品具有低风险，高回款流动性的特点，并由专业担保机构进行本金担保，适合稳健型用户的参与。',0,'固定期限，按月还本付息');
INSERT INTO `productseries` VALUES (2,'均衡型','<?xml version=\"1.0\" encoding=\"UTF-8\"?><TagCol><items><Item description=\"风险适中\" title=\"风险适中\"/><Item description=\"较高收益\" title=\"较高收益\"/></items></TagCol>','本类型产品具有中等偏上的收益，按月回款的特点，收益流动性和风险都适中，适合平衡型的的客户参与。',1,'按日计息，按月还息，到期还本，可提前还款');
INSERT INTO `productseries` VALUES (3,'进取型','<?xml version=\"1.0\" encoding=\"UTF-8\"?><TagCol><items><Item description=\"超高收益\" title=\"超高收益\"/></items></TagCol>','本类型产品具有高收益低流动性，到期还本付息的特点，适合追求高收益并能承担一定风险的客户参与。',2,'按日计息，按月还息，到期还本。可提前还款。');

insert into invite values(1, 1, 0, null, 1);