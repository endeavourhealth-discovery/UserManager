export class Application {
  id: string;
  name: string;
  description: string;
  applicationTree: string;
  isDeleted : boolean;

  getDisplayItems(): any[] {
    return [
      {label: 'Name', property: 'name'},
      {label: 'Description', property: 'description'}
    ];
  }
}
