export class ApplicationPolicy {
  id: string;
  name: string;
  description: string;
  jobCategoryId: string;
  isDeleted: boolean;

  getDisplayItems(): any[] {
    return [
      {label: 'Name', property: 'name'},
      {label: 'Description', property: 'description'}
    ];
  }
}
