**一些数据的约束和说明：**

- 后端JSON返回规范
  - 保有键：code[200/304请求成功，40X请求失败]，message[描述性文本，便于沟通]
  - 可选键：result[对于查询类请求，保存主要请求目的的响应结果]
  - 附加键：xxx[对于查询类请求，保存附加请求目的的响应结果，键值对个数不限，由前后端完全沟通后决定]

- 订单
  - 订单有四种状态，分为未完成、已完成、被用户取消、被管理员取消
  - 订单在未完成状态下才可被用户或管理员取消
  - 订单在其预定的时间段开始以后也不可被取消（也可以让其在开始时间到了以后就变成“已完成”）

## 通用功能

> 指无需登录验证的功能

- 场馆相关
  - 场馆一览（分页）
    id:venues
    paras:segment[单页限额],page[指定页码]
  - 场馆关键字搜索（分页）
    id:venue
    paras:name[关键字],segment[单页限额],page[指定页码]
  - 指定场馆信息查询（按 ID）
    id:venue-detail
    paras:id[场馆编号]
  - 指定场地信息查询（按 ID）
    id:site-detail
    paras:id[场地编号]
  - 指定场地可预约时间段查询（按 ID 和日期）
    id:site-time-list
    paras:id[场地编号],date[指定日期]
- 新闻相关
  - 新闻一览（分页，时间倒序排列）
  - 指定新闻详细内容查询
- 评论相关
  - 指定新闻评论一览（分页）
    id:news-comments
    paras:newsId[新闻编号],segment[单页限额],page[指定页码]
  - 指定场馆评论一览（分页）
    id:venue-comments
    paras:venueId[场馆编号],segment[单页限额],page[指定页码]
  - 网站评论（留言）一览（分页）
    id:global-comments
    paras:segment[单页限额],page[指定页码]

## 用户功能

- 账号相关
  - 注册
  - 登录
  - 忘记密码（申请重置密码）
  - 个人信息维护
- 场馆预约
  - 提交预约（需校验是否冲突）
- 订单一览
  - 查询该用户的所有订单（分页）
  - 取消该用户未完成的某个订单
    id:cancel
    para:userId[用户编号],reservationId[订单编号]
    pre:调用者必须保证用户和订单的匹配
- 评论管理
  - 评论网站/场馆/新闻
    id:add-global-comment
    paras:userId[用户编号],content[评论文本]
    id:add-venue-content
    paras:userId[用户编号],content[评论文本],venueId[场馆编号]
    id:add-news-comment
    paras:userId[用户编号],content[评论文本],newsId[新闻编号]
  - 修改该用户的某条评论
    id:modify-comment
    paras:userId[用户编号],commentId[评论编号],content[评论文本]
  - 删除该用户的某条评论
    id:delete-comment
    paras:id[评论编号]

## 管理员功能

- 账号相关
  - 登录
  - 个人信息维护
- 用户管理
  - 获取申请重置的用户列表
  - 同意重置密码（简单的实现，不发重置邮件，直接重置成固定值）
- 新闻管理
  - 新建（新闻标题限制 20 字，内容限制 500 字，图片至多 9 张）
  - 修改
  - 删除
- 场馆管理
  - 新建（场馆名称限制 20 字，简介限制 500 字，图片至多 9 张）
  - 修改
  - ~~删除（待考虑）~~
- 订单管理
  - 查询未完成的订单（分页）
  - 拒绝未完成的订单
  - 订单统计（统计总订单数最多的 10 个场馆）
- 评论管理
  - 删除
    id:delete-comment
    paras:id[评论编号]