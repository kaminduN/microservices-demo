import { Component, OnInit, Input } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { environment } from '../../environments/environment';
import { Product } from './../product';
// import { Input } from '@angular/core/src/metadata/directives';
import { HttpClient } from '@angular/common/http';
import { Location } from '@angular/common';

@Component({
  selector: 'app-product-details',
  templateUrl: './product-details.component.html',
  styleUrls: ['./product-details.component.css']
})
export class ProductDetailsComponent implements OnInit {
  @Input() product: Product;

  constructor(private route: ActivatedRoute, 
              private http: HttpClient,
              private location: Location) { }

  ngOnInit() {
    this.getProduct();
  }

  getProduct(): void {
    //https://stackoverflow.com/a/45339049
    const id = this.route.snapshot.paramMap.get('id');
    console.log(id);
    
    // const url = env${id;
    // return this.http.get<Hero>(url).pipe(
    //   tap(_ => this.log(`fetched hero id=${id}`)),
    //   catchError(this.handleError<Hero>(`getHero id=${id}`))
    // );
    this.fetchProduct(id);
    // this.getProduct(id)
    //   .subscribe(product => this.product = product);
  }

  fetchProduct(id: string){
      console.log(environment.apiUrl +  environment.productApi + "/" + id);
    
      this.http.get<Product>(environment.apiUrl +  environment.productApi  + "/" + id)
        .subscribe(res => {
              console.log(res);
              this.product = res;
              // this.product.name = res.
            },
            err => {
              console.log('Error occured');
            }
      );
  
    };
  
    goBack(): void {
      this.location.back();
    }

    deleteProduct(id: string): void{

      // actual delete operation
      this.http.delete(environment.apiUrl +  environment.productApi  + "/" + id)
      .subscribe(res => {
            console.log(res);
            // this.product.name = res.
          },
          err => {
            console.log('Error occured while deleting..');
          }
    );
      this.goBack();
    }
}
