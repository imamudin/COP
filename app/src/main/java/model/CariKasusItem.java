package model;

/**
 * Created by agung on 12/03/2016.
 */
public class CariKasusItem {
    private String nama_kasus, nama_pelapor,no_lp,tgl_kasus, kasus_id;

    public CariKasusItem(){

    }

    public String getNama_kasus() {
        return nama_kasus;
    }

    public void setNama_kasus(String nama_kasus) {
        this.nama_kasus = nama_kasus;
    }

    public String getNama_pelapor() {
        return nama_pelapor;
    }

    public void setNama_pelapor(String nama_pelapor) {
        this.nama_pelapor = nama_pelapor;
    }

    public String getNo_lp() {
        return no_lp;
    }

    public void setNo_lp(String no_lp) {
        this.no_lp = no_lp;
    }

    public String getTgl_kasus() {
        return tgl_kasus;
    }

    public void setTgl_kasus(String tgl_kasus) {
        this.tgl_kasus = tgl_kasus;
    }

    public String getKasus_id() {
        return kasus_id;
    }

    public void setKasus_id(String kasus_id) {
        this.kasus_id = kasus_id;
    }
}
