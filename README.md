---
title: Gospel Of Dogs
# tags: Templates, Talk
# description: View the slide with "Slide Mode".
---

# Gospel Of Dogs 

[![hackmd-github-sync-badge](https://hackmd.io/cbRQrP0UQ0OlV0fU5v6WmA/badge)](https://hackmd.io/cbRQrP0UQ0OlV0fU5v6WmA)


一款專為狗狗飼主與想要養狗的人設計的app

---

Gospel Of Dogs 為大學專題時製作的Project，在這邊做個紀錄


---

## app畫面預覽



| 登入介面 | 日誌動態牆 |附近診所|個人介面|
| -------- | -------- |-------- |-------- |
|![image](https://i.imgur.com/aVH1ykv.png) |![image](https://i.imgur.com/gJ9fwSy.png)|||


<!-- *****![](https://i.imgur.com/ij69tPh.png)
![](https://i.imgur.com/ij69tPh.png)*****
- Front-end developer
- VSCode :heart: 
- I use tabs. :cat: 
 -->
---

## 功能大覽

- 登入介面
  -會員註冊
  -忘記密碼 
- 日誌動態
  - 用戶搜尋
- 用戶介面
  - 編輯個人資訊
  - 追隨/被追隨查看
- 附近診所
- 日程提醒
- 
---
### 使用api及開發工具
- ****Googl Map API****
- ****Firebase Firestore****
- ****SQLite****
- ****AndroidStudio****

---

## 功能介紹及操作畫面
| 功能 | 介紹 | 操作畫面 |其他|
| -------- | -------- | -------- |--------|
|開場|由於打開app進入到主介面需要稍微的等待，因此放了一個開頭畫面在前面，減少使用者等待的體感時間。|![image](https://i.imgur.com/M5F8pIC.gif =440x)||
|註冊+一般登入|使用者將信箱密碼等資訊填入完成後，看到註冊成功後的提醒後，便可進行一般登入|![image](https://i.imgur.com/nXL1yDa.gif =220x)||
|Google登入|使用者除一般登入外也可以選擇點擊下方按鈕，用google帳號直接進行登入|![image](https://i.imgur.com/HDJMfk3.gif =220x)||
|時程提醒|使用者可以在我們的提醒功能中建立提醒事件，APP將在特定的時間提醒狗主人該去做的事，點擊加號進入新增提醒畫面後，使用者需先輸入標題，而後於事件、日期、提醒週期、週期單位、開啟狀態等欄位進行選擇，事件欄位有許多事件可以選擇，諸如洗澡、餵食、除蟲、打預防針……等，選擇不同的事件，能在提醒功能的主頁面中看到清楚易懂的ICON圖示，而使用者亦可在提醒功能主畫面中清楚的看到提醒週期、時間、開啟狀態等資訊。如欲編輯提醒，直接點擊該提醒項目，便會進入編輯畫面，點擊工具欄打勾圖案後，便會儲存。如欲刪除提醒，便可於項目上進行長壓，上方工具欄便會出現垃圾桶圖示，點擊即可刪除。|![image](https://i.imgur.com/ccgmCtX.gif =220x)||
|訊息欄位|若有人追蹤你、喜歡你得的日誌或進行留言，便可以在這邊看到，點擊便可進入到該用戶專頁或該篇日誌|![image](https://i.imgur.com/p8gooGe.gif =220x)||
|個人編輯|編輯個人名稱、暱稱、頭像及自介部分|![image](https://i.imgur.com/K9mrkum.gif  =220x)||
|最新/熱門|在熱門可以看到依照喜歡數排序的日誌，最新則是按照發布時間排序|![image](https://i.imgur.com/LMXBw1S.gif =220x)||
|查詢用戶|可直接瀏覽查看其他用戶的個人專頁，也可透過用戶名稱進行查詢|![image](https://i.imgur.com/z9FY8HE.gif =220x)||
|留言|可在其他用戶的日誌下進行留言|![image](https://i.imgur.com/biuHpQI.gif =220x)||
|百科|使用者可於不同分類中點擊想看的狗類資訊，進一步的觀看詳細的內容，內容皆源於各大獸醫診所及寵物報導專欄，如果有喜歡的內容，可以點擊在畫面右上角的愛心進行收藏，日後便可直接在收藏分類中直接觀看|![image](https://i.imgur.com/tRvgZ9Z.gif =220x)||
|上傳/編輯日誌|用戶可點擊下方功能欄位中的發佈，進行寵物日誌的上傳，首先選擇照片後進行裁減切編輯，而後填入日誌內文及選擇放置相簿，便可進行上傳，而後可點擊日誌右上方的更多圖示，去編輯內文|![image](https://i.imgur.com/tSoaP95.gif =220x)||
|移動/刪除日誌|點擊個人日誌右上方的更多按鈕後，便可將日誌移動到不同狗的相簿中，也可以選擇刪除|![image](https://i.imgur.com/1aBqY6f.gif =220x)||
|收藏日誌|點擊其他用戶的日誌右下方書籍圖示，便可在個人專頁的書籍圖示中查看收藏的日誌|![image](https://i.imgur.com/BAeJNcW.gif =220x)||
|診所|使用了政府OpenData平臺的全台獸醫診所資訊，並透過Android Studio提供的volley library 中的方法取的其json檔，於Google Map上進行標記，使用者可以在APP上方的搜尋欄位輸入想查詢的位置或使用GPS定位，Map 的camera便會轉移至該位置，並顯示附近的診所，點擊圖標後會顯示診所資訊|![image](https://i.imgur.com/cbzqJGu.gif =220x)||
|忘記密碼|使用者若是忘記密碼，可以再登入介面點擊忘記密碼，進入畫面後填入信箱，註冊信箱便會收到更改密碼的郵件，供使用者更改密碼|![image](https://i.imgur.com/G1vZBZW.gif =220x)||
|遊戲|請操作狗兒小黃越過所有危險物(不能吃的食物)|![image](https://i.imgur.com/nMyfuIu.gif =220x)||
|推播|若有人追蹤你、喜歡你得的日誌或進行留言手機便會跳出推播訊息，點擊便會直接進入指定畫面|![image](https://i.imgur.com/QKozGNI.gif =220x)||
|關閉推播|可在設定頁面中關閉推播開關，下次便不會跳出推播訊息|![image](https://i.imgur.com/hrPTDaw.gif =220x)||
|登出|用戶可在設定頁面中進行登出|![image](https://i.imgur.com/hx5fxls.gif =220x)||
|更改密碼|若想更改密碼，便可以在設定頁面中點擊更改密碼，寄送更改密碼信件到用戶信箱|![image](https://i.imgur.com/GoB81wS.gif =220x)||
|兩次退出程式|為避免用戶不小心點擊返回跳出app，用戶第一次按返回時會跳出提醒，若短時間再按第二次便可以成功退出app|![image](https://i.imgur.com/wjDGsaI.gif =220x)||
蒐集了各大獸醫診所及寵物報導專欄的狗類資訊




<!-- --- -->
<!-- ### 70% of our users are developers. Developers :heart: GitHub. -->
<!-- {%youtube E8Nj7RwXf0s %} -->


---



### 登入介面
在登入介面可以進行一般登入跟google帳戶登入，另外有註冊及忘記密碼等功能可使用，輸入信箱密碼的地方有設置輸入的監聽，若格式不符，旁邊便會出現提醒方塊，做為防呆用。

---
### 日誌分享主介面 
登入app後會出現的第一個主要介面，在這邊你可以看到追蹤的用戶分享的相關日誌，按下愛心或點擊進入留下你的感想。

---
### 日誌分享熱門區 
在日誌分享介面中，會有三個分區可以選擇，點擊其中熱門分類，便可以看到根據熱門程度進行排行的寵物日誌。

---


### 寵物日程提醒主介面 

---

### 區域獸醫診所查找主介面 
點擊進入後便會顯示GOOGLE地圖的介面，你可以從上面的搜尋欄位輸入想查詢的位置，便會將參數傳遞給GOOGLE API，並回傳位置，便可以看到該地區的寵物診所分布及政府OPEN DATA提供的資訊。

---
### 養狗知識百科
點擊進入後便可以看到專門提供給飼主的養狗知識

---
### 推播



---


## 製作遇到的困難
Bitmap太大結果沒能顯示
提醒有時並不會在準確時間發出提醒
- 地圖
地圖要標示的診所太多，導致app UI幾乎不能動作
json object /array 取得寫法不同
-  


## :tada: 感想
Android app開發的過程中可謂是步步皆坑，在寫這個app之前，其實也只在課堂上寫過猜數字的app，初期只知道要做某些功能，然而要怎麼做這件事，完全一頭霧水，很多東西都得從零學起，UI介面怎麼拉怎麼寫，資料庫，生命週期，一邊看著教學一邊coding，慢慢去摸索，也因此過程處處撞牆，常因為一些莫名其妙的錯誤訊息，徹夜debug，還好有Stack Overflow 、 iT 邦幫忙 、github、CSDN、FB/YouTube這些良師益友，有想做什麼功能或遇到什麼錯誤訊息，絕大多數都可以在上面找到值得參考的資訊，就連平時看到怕的錯誤訊息出現都不再感到畏懼了呢(笑)，

有時候想改個part，卻總是發生找不到的錯誤

邏輯錯誤更麻凡

<!-- ## Content script

- Bind with each page
- Manipulate DOM
- Add event listeners
- Isolated JavaScript environment
  - It doesn't break things
 -->


<!-- ---

<style>
code.blue {
  color: #337AB7 !important;
}
code.orange {
  color: #F7A004 !important;
}
</style>

- <code class="orange">onMessage('event')</code>: Register event listener
- <code class="blue">sendMessage('event')</code>: Trigger event

---
```typescript
import * as Channeru from 'channeru'

// setup channel in different page environment, once
const channel = Channeru.create()
```
--- -->
