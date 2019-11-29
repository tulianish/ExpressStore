package mobile.computing.expressstore;

public class Items_Model {
    /**
     * Items_Model- This class is model for saving product added to the cart. It contains the getter and setter functions.
     *
     * Attributes:
     *      img (String): The resource url of the product image
     *      name (String): The name of the product
     *      price (double): The price of the product
     *      sale_price (double): The price of the product when on sale
     *      qty (int): The quantity of the product
     */

    String proid;
    String img;
    String name;
    double price;
    double sale_price;
    int qty = 1;

    public Items_Model(String proid,String img, String name, double price, double sale_price, int qty) {
        this.proid = proid;
        this.img = img;
        this.name = name;
        this.price = price;
        this.sale_price = sale_price;
        this.qty = qty;
    }

    public String getProid() {
        /**
         * getProid function
         *
         * @return String ID of the product
         */
        return proid;
    }

    public String getImg() {
        /**
         * getImg function
         *
         * @return int resource url of the product image
         */
        return img;
    }

    public String getName() {
        /**
         * getName function
         *
         * @return String name of the product
         */
        return name;
    }

    public double getPrice() {
        /**
         * getPrice function
         *
         * @return double price of the product
         */
        return price;
    }

    public double getSale_price() {
        /**
         * getSale_price function
         *
         * @return double price of the product when on sale
         */
        return sale_price;
    }

    public int getQty() {
        /**
         * getQty function
         *
         * @return int quantity of the product
         */
        return qty;
    }

    public void setProid(String proid) {
        /**
         * setProid function
         *
         * @param String set ID of the product
         */
        this.proid = proid;
    }

    public void setImg(String img) {
        /**
         * setImg function
         *
         * @param img set resource url of the product image
         */
        this.img = img;
    }

    public void setName(String name) {
        /**
         * setName function
         *
         * @param name set name of the product
         */
        this.name = name;
    }

    public void setPrice(Double price) {
        /**
         * setPrice function
         *
         * @param price set price of the product
         */
        this.price = price;
    }

    public void setSale_price(Double sale_price) {
        /**
         * setSale_price function
         *
         * @param sale_price set price of the product when on sale
         */
        this.sale_price = sale_price;
    }

    public void setQty(int qty) {
        /**
         * setQty function
         *
         * @param qty set quantity of the product
         */
        if(qty==0){
            this.qty=1;
        }else {
            this.qty = qty;
        }
    }
}
