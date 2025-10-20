import {Injectable} from '@angular/core';
import {Subject} from "rxjs";
import {ProductOrders} from "../models/product-orders.model";
import {HttpClient} from "@angular/common/http";
import {ProductOrder} from "../models/product-order.model";

@Injectable()
export class EcommerceService {
  private productsUrl = "/api/products";
  private ordersUrl = "/api/orders";

  private productOrder: ProductOrder;
  private orders: ProductOrders = new ProductOrders();

  private productOrderSubject = new Subject<ProductOrder>();
  private ordersSubject = new Subject<ProductOrders>();
  private totalSubject = new Subject<number>();

  private total: number;

  ProductOrderChanged = this.productOrderSubject.asObservable();
  OrdersChanged = this.ordersSubject.asObservable();
  TotalChanged = this.totalSubject.asObservable();

  constructor(private http: HttpClient) {
  }

  getAllProducts() {
    return this.http.get(this.productsUrl);
  }

  saveOrder(order: ProductOrders) {
    return this.http.post(this.ordersUrl, order);
  }

  get SelectedProductOrder() {
    return this.productOrder;
  }

  set SelectedProductOrder(value: ProductOrder) {
    this.productOrder = value;
    this.productOrderSubject.next(this.productOrder);
  }

  get ProductOrders() {
    return this.orders;
  }

  set ProductOrders(value: ProductOrders) {
    this.orders = value;
    this.ordersSubject.next(this.orders);
  }

  get Total() {
    return this.total;
  }

  set Total(value: number) {
    this.total = value;
    this.totalSubject.next(this.total);
  }
}
