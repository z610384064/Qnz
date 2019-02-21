package com.rd.qnz.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/10/9 0009.
 */

public class CrashRecordBean {




        /**
         * cashDetail : [{"addtime":1507103547000,"bankName":"建设银行","bankNo":"6196","id":212630,"money":301.28,"status":"1","statusDesc":"提现成功"},{"addtime":1506759882000,"bankName":"建设银行","bankNo":"6196","id":212172,"money":100,"status":"1","statusDesc":"提现成功"},{"addtime":1506544732000,"bankName":"建设银行","bankNo":"6196","id":211014,"money":100,"status":"1","statusDesc":"提现成功"},{"addtime":1505126586000,"bankName":"建设银行","bankNo":"6196","id":206917,"money":100,"status":"1","statusDesc":"提现成功"},{"addtime":1505122316000,"bankName":"建设银行","bankNo":"6196","id":206868,"money":100,"status":"1","statusDesc":"提现成功"},{"addtime":1505119810000,"bankName":"建设银行","bankNo":"6196","id":206843,"money":100,"status":"1","statusDesc":"提现成功"},{"addtime":1502587940000,"bankName":"建设银行","bankNo":"6196","id":200495,"money":103.79,"status":"1","statusDesc":"提现成功"},{"addtime":1500514352000,"bankName":"建设银行","bankNo":"6196","id":196332,"money":202.22,"status":"1","statusDesc":"提现成功"},{"addtime":1498464163000,"bankName":"建设银行","bankNo":"6196","id":191932,"money":101.13,"status":"1","statusDesc":"提现成功"},{"addtime":1497182049000,"bankName":"建设银行","bankNo":"6196","id":188472,"money":403.8,"status":"1","statusDesc":"提现成功"}]
         * currentPage : 1
         */

        private int currentPage;
        private List<CashDetailBean> cashDetail;

        public int getCurrentPage() {
            return currentPage;
        }

        public void setCurrentPage(int currentPage) {
            this.currentPage = currentPage;
        }

        public List<CashDetailBean> getCashDetail() {
            return cashDetail;
        }

        public void setCashDetail(List<CashDetailBean> cashDetail) {
            this.cashDetail = cashDetail;
        }

        public static class CashDetailBean implements Serializable {
            /**
             * addtime : 1507103547000
             * bankName : 建设银行
             * bankNo : 6196
             * id : 212630
             * money : 301.28
             * status : 1
             * statusDesc : 提现成功
             */

            private long addtime;
            private String bankName;
            private String bankNo;
            private int id;
            private String money;
            private String status;
            private String statusDesc;

            public long getAddtime() {
                return addtime;
            }

            public void setAddtime(long addtime) {
                this.addtime = addtime;
            }

            public String getBankName() {
                return bankName;
            }

            public void setBankName(String bankName) {
                this.bankName = bankName;
            }

            public String getBankNo() {
                return bankNo;
            }

            public void setBankNo(String bankNo) {
                this.bankNo = bankNo;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getMoney() {
                return money;
            }

            public void setMoney(String money) {
                this.money = money;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public String getStatusDesc() {
                return statusDesc;
            }

            public void setStatusDesc(String statusDesc) {
                this.statusDesc = statusDesc;
            }
        }

}
