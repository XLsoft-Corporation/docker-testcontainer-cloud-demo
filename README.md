# docker-testcontainer-cloud-demo

Testcontainers Cloud のデモ環境です。

このドキュメントでは、Testcontainers Cloud を GitHub Actions から利用して Java のコードをテストします。

## 環境設定

ログイン後 Profile を選択すると初期画面に移行します。

Continuous Integration (CI) を選択し、GitHub Actions を選択します。

### Testcontainers のサービスアカウントを作成

サービスアカウントを作成します。例として `xlsoft-tcc-account` を作成しました。

Access Token が生成されるのでコピーして保存しておきます。

`aj_tcc_svc_LzLzFH2_S664PC1roLEdBavSupy3h0M1H0vwqZQR-4dg9`


### GitHub のシークレットを設定

GitHub のレポジトリで「Settings＞Secrets and variables＞Actions」で「Repository secrets」を作成してみましょう。


