// The file contents for the current environment will overwrite these during build.
// The build system defaults to the dev environment which uses `environment.ts`, but if you do
// `ng build --env=prod` then `environment.prod.ts` will be used instead.
// The list of which env maps to which file can be found in `.angular-cli.json`.

export const environment = {
  production: false,
  title: 'app-dev',
  apiUrl: 'http://ELB-product-api-400783613.us-east-2.elb.amazonaws.com:8080/api',
  contentApiUrl: 'http://ELB-product-api-400783613.us-east-2.elb.amazonaws.com:8080',
  productApi: '/products',
  contentApi: '/files/',
  defaultimageUrl:'assets/images/testProduct.png',
  placeholdImage: 'assets/images/default.png'
};
