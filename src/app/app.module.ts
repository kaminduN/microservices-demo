import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';

import { AppComponent } from './app.component';
import { CreateProductComponent } from './create-product/create-product.component';
import { ViewProductComponent } from './view-product/view-product.component';

import {HttpClientModule} from '@angular/common/http';

import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ProductDetailsComponent } from './product-details/product-details.component';
import { PageNotFoundComponent } from './not-found.component';

@NgModule({
  declarations: [
    AppComponent,
    CreateProductComponent,
    ViewProductComponent,
    ProductDetailsComponent,
    PageNotFoundComponent
  ],
  imports: [
    BrowserModule,

    // Include HttpClientModule under 'imports' in your application module
    // after BrowserModule.
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,
    AppRoutingModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
