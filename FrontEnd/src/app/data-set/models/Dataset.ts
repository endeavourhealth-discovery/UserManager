export class DataSet {
	uuid: string;
	name: string;
	description: string;
	attributes: string;
	queryDefinition: string;
	dpas: { [key: string]: string; };

	getDisplayItems(): any[] {
		return [
			{label: 'Description', property: 'description'}
		];
	}
}
