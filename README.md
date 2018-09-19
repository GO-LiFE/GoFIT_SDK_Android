[![GOLiFE Logo](http://www.goyourlife.com/images/common/logo.png)](http://www.goyourlife.com)

# GoFIT SDK for Android — [GOLiFE 手環](http://www.goyourlife.com/zh-TW/productlist/#health) App 介接 SDK

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

此為 [GOLiFE](http://www.goyourlife.com) 釋出之官方 Android App SDK (`Community Edition`).

讓任何人都可以藉由本 SDK，直接拿 [GOLiFE 市售手環](http://www.goyourlife.com/zh-TW/productlist/#health) 就可以連接、取資料。

(尋找 iOS 版嗎? 請到 [此處](/../../../GoFIT_SDK_iOS))

## What's This?
- Android App 的開發人員，可藉由本 SDK 的整合，便可輕輕鬆鬆快速完成開發。
- 並且，即刻就可直接與市面上 [GOLiFE](http://www.goyourlife.com) 出品銷售之 GOLiFE [Care 系列手環](http://www.goyourlife.com/zh-TW/productlist/#health) 裝置連接。達成包括連線、配對、同步、設定等等各項功能。
- 使用範例 : 官方版 GOLiFE's [GoFIT App (Google Play 下載)](https://play.google.com/store/apps/details?id=com.golife.fit&hl=zh_TW).


## 從哪開始?? 
1. 請到 [Release 區](/../../releases) 下載 : 包括 Demo App 的 source code (壓縮檔) 與 SDK libraray (.aar)

2. 再請到 [GoFIT SDK 申請表](https://docs.google.com/forms/d/1WutpWDV6VlGUhq2RZs2takjcGKHctG2GYfNQr81CA-0/) 申請試用憑證
    - 請不用擔心，僅是請您留個聯絡方式，只需填寫 email 或電話大名等等，然後選擇您要搭配使用的裝置即可。我們收到後便會馬上回覆 30 天的試用憑證給您~
    - 非常歡迎 **為學術研究用途提供更長免費效期** -- 請您只需在申請表上註明，我們都非常樂意為您提供
    - 請參見我們的 [定價方案](http://dev.goyourlife.com/)。如果您有意用於 **商業用途或是客製合作**，請您在申請表上註明，或歡迎您來信 [聯繫我們](http://www.goyourlife.com/zh-TW/feedback/)

3. 將收到的憑證，整合進您的 App 內 &rArr; 請參考我們的 Wiki : [Demo App (SDK 的安裝與 compile)](/../../wiki/Demo-App-(SDK-%E7%9A%84%E5%AE%89%E8%A3%9D%E8%88%87-compile))

4. Then you are good to go!!! :grin: 就是這麼簡單~
    - (當然囉，您手邊必須要先有一台我們的裝置 :stuck_out_tongue_closed_eyes: — [GOLiFE 手環](http://www.goyourlife.com/zh-TW/productlist/#health))
    - 還有，首次啟動，會對您收到的憑證做驗證，並下載授權 &rArr; 所以，首次啟動請要連網喔!!!~

請參見我們的 [定價方案](http://dev.goyourlife.com/) &rArr; **商業用途或是客製合作**，都非常歡迎您來信 [聯繫我們](http://www.goyourlife.com/zh-TW/feedback/)。


## Demo App
請參考我們的 [Wiki](/../../wiki) : [Demo App (SDK 的安裝與 compile)](/../../wiki/Demo-App-(SDK-%E7%9A%84%E5%AE%89%E8%A3%9D%E8%88%87-compile))


## API SPEC
請參考我們的 [Wiki](/../../wiki) : [API SPEC](/../../wiki/GoFIT-SDK-Android-Application-Programming-Interface-Specifications)


## SDK 支援之 features 

#### Care 系列手環

|            | Care         | Care-X     | Care-X HR  | Care Xc    | Care Xe    | 
|:-----------|:-------------|:-----------|:-----------|:-----------|:-----------|
| 訊息通知 | <ul><li>- [ ] </li></ul> | <ul><li>- [ ] </li></ul> | <ul><li>- [ ] </li></ul> | <ul><li>- [ ] </li></ul> | <ul><li>- [ ] </li></ul> |
| 基本資料輸入 | <ul><li>- [x] </li></ul> | <ul><li>- [x] </li></ul> | <ul><li>- [x] </li></ul> | <ul><li>- [x] </li></ul> | <ul><li>- [x] </li></ul> |
| 步數目標 | <ul><li>- [x] </li></ul> | <ul><li>- [x] </li></ul> | <ul><li>- [x] </li></ul> | <ul><li>- [x] </li></ul> | <ul><li>- [x] </li></ul> |
| 公英制 | <ul><li>- [x] </li></ul> | <ul><li>- [x] </li></ul> | <ul><li>- [x] </li></ul> | <ul><li>- [x] </li></ul> | <ul><li>- [x] </li></ul> |
| 12/24 時制 | <ul><li>- [x] </li></ul> | <ul><li>- [x] </li></ul> | <ul><li>- [x] </li></ul> | <ul><li>- [x] </li></ul> | <ul><li>- [x] </li></ul> |
| 左右手 | <ul><li>- [x] </li></ul> | <ul><li>- [x] </li></ul> | <ul><li>- [x] </li></ul> | <ul><li>- [x] </li></ul> | <ul><li>- [x] </li></ul> |
| 抬手點亮 | <ul><li>- [x] </li></ul> | <ul><li>- [x] </li></ul> | <ul><li>- [x] </li></ul> | <ul><li>- [x] </li></ul> | <ul><li>- [x] </li></ul> |
| 久坐提醒 | <ul><li>- [x] </li></ul> | <ul><li>- [x] </li></ul> | <ul><li>- [x] </li></ul> | <ul><li>- [x] </li></ul> | <ul><li>- [x] </li></ul> |
| 防丟提示 | <ul><li>- [x] </li></ul> | <ul><li>- [x] </li></ul> | <ul><li>- [x] </li></ul> | <ul><li>- [x] </li></ul> | <ul><li>- [x] </li></ul> |
| 勿擾模式 |    |    | <ul><li>- [x] </li></ul> | <ul><li>- [x] </li></ul> | <ul><li>- [x] </li></ul> |
| 心率定時偵測 |    |    | <ul><li>- [x] </li></ul> | <ul><li>- [x] </li></ul> | <ul><li>- [x] </li></ul> |
| 心率警示 |    |    |    | <ul><li>- [x] </li></ul> | <ul><li>- [x] </li></ul> |
| 尋找手環 |    |    |    | <ul><li>- [ ] </li></ul> | <ul><li>- [ ] </li></ul> |
| 尋找手機 |    |    |    | <ul><li>- [ ] </li></ul> | <ul><li>- [ ] </li></ul> |
| 遙控拍照 |    |    |    | <ul><li>- [ ] </li></ul> | <ul><li>- [ ] </li></ul> |
| 螢幕鎖 |    |    |    | <ul><li>- [x] </li></ul> | <ul><li>- [x] </li></ul> |
| 鬧鐘 | <ul><li>- [x] 30 組 </li></ul> | <ul><li>- [x] 30 組 </li></ul> | <ul><li>- [x] 30 組 </li></ul> | <ul><li>- [x] 30 組 </li></ul> | <ul><li>- [x] 30 組 </li></ul> |
| 自動睡眠偵測 |    |    | <ul><li>- [x] </li></ul> | <ul><li>- [x] </li></ul> | <ul><li>- [x] </li></ul> |
| 可同步項目 | <ul><li>- [x] 步數</li><li>- [x] 睡眠</li></ul> | <ul><li>- [x] 步數</li><li>- [x] 睡眠</li></ul> | <ul><li>- [x] 步數</li><li>- [x] 睡眠</li><li>- [x] 心率</li><li>- [ ] 血氧</li></ul> | <ul><li>- [x] 步數</li><li>- [x] 睡眠</li><li>- [x] 心率</li></ul> | <ul><li>- [x] 步數</li><li>- [x] 睡眠</li><li>- [x] 心率</li></ul> |


## 有任何疑問嗎?
歡迎您到 [Issues 區](/../../issues) 留言~ 我們會第一時間回答您。


## Copyright and License
此為 `Community Edition`.

智慧財產權為 [GOLiFE](http://www.goyourlife.com) 所有。

&copy; 2018 GOYOURLIFE INC. 

http://www.goyourlife.com

[![GoFIT SDK](./GoFIT_SDK.png)](http://www.goyourlife.com)
