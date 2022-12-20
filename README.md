# オンラインチャットシステム

 ・このシステムは、3人以上が同時にチャットやファイル送信ができるものである。  
 ・最初は使いやすく、Chatworkみたいなコミュニケーションツールを作りたいと考えたが、今回は最低限のチャット、ファイル送信機能が実現するシステムを作成した。   
・Javaで開発して、C/Sモデルを使ってサーバー転送によりチャットの機能を実現した。  
・クライアントパッケージ、サーバーパッケージ、コモンパッケージの3つに分ける。

## システム説明
  
 ・このシステムはTCP通信をベースとし、クライアントとサーバーが同時にアクセスできるクライアント・サーバーモデルを使う。  
 ・スレッド技術により、複数ユーザーが同時にオンラインできることで、システムの並行性を高め、実用性を高めている。  
 ・シンプルなクライアント画面があり、メッセージ配信の安定性と一貫性を確保するため、信頼性の高いTCP通信を使用している。  
 ・ソケット通信を使用するため、双方向にリアルタイムで通信をすることが可能。  

## システム機能  

  - クライアントがユーザー名を入力し、ログインする    
  - サーバーがログイン情報を確認する。  
  - クライアント間の全員チャット  
  - クライアント間の個別チャット  
  - オンラインリストの動的な更新  
  - オンライン人数を表示  
  - クライアントログアウト処理  
  - クライアントオンライン、オフラインアラーム  
  - クライアントファイル転送
  - ウィンドウ上のメッセージをクリアする

## 開発環境
 - Windows
 - Eclipse
 - JavaSE17
  
## Demo 
- ユーザー登録：ユーザー名入力画面(ここでaaaとbbbで登録する)    
![](https://user-images.githubusercontent.com/108509511/208335136-0af5c656-943a-46c9-877c-41a32d978c37.png)

- ユーザー名が同じ「aaa」にすると、登録できない  
![スクリーンショット (122)](https://user-images.githubusercontent.com/108509511/208356559-27930950-1cd8-480d-96dd-1534c5e0f6d6.png)

- aaaが先に入室して、bbbが続いて登録した場合、aaaの画面上に「bbbが入室しました」というメッセージが提示される  
- 右下にオンライン人数が自動的に更新される  
![スクリーンショット (124)](https://user-images.githubusercontent.com/108509511/208358887-891bd77f-0d2a-41ee-bda2-ed2abf1dd13e.png)

- 全員送信：送信対象が「ALL」に選択すれば、「aaa」のクライアントから送信したメッセージが、チャットルームにいる全員が受信できる
![スクリーンショット (126)](https://user-images.githubusercontent.com/108509511/208359586-3bcd8893-e026-4184-9b4b-b59e5b084957.png)

- 個別送信：送信対象が「bbb」に選択すれば、「aaa」のクライアントから送信したメッセージが、「bbb」のクライアントだけが受信できる  
![スクリーンショット (128)](https://user-images.githubusercontent.com/108509511/208362232-803fef76-215d-4fdd-87fb-2bc5d391b71f.png)

- メッセージが「Chat」から「file」に切り替えると、ファイル送信ができる  
![スクリーンショット (130)](https://user-images.githubusercontent.com/108509511/208366817-407b275f-356c-4410-a22c-bedf7f02ffe4.png)

- 受信側「bbb」のクライアントは「aaa」のクライアントからのファイル保存するかどうか選択できる  
![スクリーンショット (132)](https://user-images.githubusercontent.com/108509511/208367283-1c1aaf30-871b-441c-b07d-e71456e8d572.png)

- 保存する場合、保存する場所が選択する  
![スクリーンショット (134)](https://user-images.githubusercontent.com/108509511/208367691-575dfa3a-5519-40b3-9442-2a32beba13eb.png)

- 保存する場所に既に同じ名前のファイルが存在する場合、「上書きしますか」を提示する  
![スクリーンショット (136)](https://user-images.githubusercontent.com/108509511/208368097-3e56ff99-c6af-49ad-a4e1-115a4bfd87a1.png)

- 「bbb」のクライアントが退室する場合、一度確認ウィンドウが表示される  
![スクリーンショット (138)](https://user-images.githubusercontent.com/108509511/208371251-e585c3a6-42b6-430b-82d2-ceec12a3837a.png)

- 「bbb」のクライアントが退室すると、「aaa」のクライアント上に「bbb」が退室するメッセージが表示される  
- オンライン人数も更新される  
![スクリーンショット (140)](https://user-images.githubusercontent.com/108509511/208371715-0a5ea36c-7094-4cf4-a8e7-ceeca42ca98c.png)



## パッケージ説明 

・client：    
  1.ログイン機能：ユーザーは登録後、サーバーを選択してチャットルームにログインすることができる。    
  2.ユーザーを表示する：オンラインユーザーをリストに表示する。    
  3.メッセージ受信：他のユーザーからのメッセージを受信する機能。    
  4.メッセージの送信：ユーザーがメッセージを送信する機能。  
  
・server:  
  1.ログイン情報精査：ログイン情報が正しいかどうかを確認し、正しい場合はログイン情報をクライアントに返却する。ユーザーのログインが許可される。　　  
  2.オンライン人数を表示する：オンライン中の各ユーザーにオンライン人数を送信する。  
  3.チャットメッセージの転送：オンライン中の全ユーザーにメッセージを転送する。  
  
 ・common：  
  1.メッセージタイプを定義する。  
  2.メッセージ転送メソッドを提供する。  
  
 
 ## モジュール、クラス説明
・client  
　Clientモジュール：サーバー側との接続を確立し、クライアントログインを完了し、全員チャット、個別チャット、ユーザーリスト更新、ファイル変換などの機能を実現する。  
　ClientUIモジュール：クライアントウィンドウのインターフェース、メッセージ表示、ユーザーリスト、メッセージタイプ、オンライン人数、その他の情報をウィンドウに表示する。  
　ClientMsgTypeモジュール：共通のメッセージ処理方式を定義する。  

・service  
　Serverモジュール：リスナーを起動し、クライアントとの接続を確立する。  
　ServerHandlerモジュール：スレッド管理を行い、複数ユーザーの管理可能、各ユーザーが送信したメッセージは、適宜処理された後、クライアントに転送される。  
　ServerMsgTypeモジュール：クライアントから送信されたメッセージを処理し、メッセージのみを転送する。1対多と1対1の2つのモードがある。  

・common  
　Messageクラス：クライアントとサーバーが転送するメッセージをクラスとして定義し、Messageオブジェクトのシリアライズにより転送する。    
　Commonクラス：ファイルシリアライズの処理と共通機能関数を提供する。  
　Configクラス：接続先のIPとポート番号に関する設定情報を含む。  

## インストール
$ git clone https://github.com/le-wangyuting/chat.git

## 使い方
動作環境：すべて可能（今回はWindows環境だけ検証した）  
以下の手順による実行：
1.プロジェクトをダウンロードし、ロカールに保存する。  
2.serviceフォルダを開いて、Server.javaを起動する。  
3.clientフォルダを開いて、Client.javaを起動する。

## まとめ  
 　今回はJavaを使ってTCP通信を利用するチャットシステムを実現した。最初に設計したプログラムは、クライアントとサーバーの間だけの通信ができるなものでした。その後、同時アクセスは実装されていないことが判明した。そして、調べたらクライアント側の処理をスレッド化し、複数のクライアントがオンラインで同時に応答できるようにした。そこで、Javaのオブジェクトのシリアライズを利用してMessageクラスに統一し、オブジェクトの入出力ストリームを使ってクライアントとサーバーの間で転送するようにしました。

 　また技術力が不足しているため、まだ実現できないことがある。  
・クライアントウィンドウは、時間の関係でシンプルになりすぎてしまい、丁寧に設計していなかった。  
・3人以上を使うことが可能だが、最大何人までできるのかはまだ検証していない。    
・異なる機器がこのシステムで相互に通信できるかは検証していない。    

 
