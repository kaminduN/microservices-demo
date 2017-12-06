import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { CreateProductComponent } from './create-product/create-product.component';
import { ViewProductComponent } from './view-product/view-product.component';
import { ProductDetailsComponent } from './product-details/product-details.component';
import { PageNotFoundComponent } from './not-found.component';

const routes: Routes = [
  {
    path: 'view',
    component: ViewProductComponent,
    children: []
  },
  {
    path: 'create',
    component: CreateProductComponent,
    children: []
  },
  { path: 'product/:id',
    component: ProductDetailsComponent 
  },
  { path: '',   redirectTo: '/view', pathMatch: 'full' },
  { path: '**', component: PageNotFoundComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
