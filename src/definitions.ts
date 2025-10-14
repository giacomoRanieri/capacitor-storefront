export interface Storefront {
  /**
   * Country code of the current store account.
   */
  readonly countryCode: string;
}
export interface CapacitorStorefrontPlugin {
  /**
   * Initialize the underlying store client
   */
  initialize(): Promise<void>;

  /**
   * Gets the storefront for the current store account.
   * @return {Promise<Storefront>} A promise of a Storefront object.
   * The promise will be rejected if configure has not been called yet or if storefront could
   * not be obtained for account.
   */
  getStorefront(): Promise<Storefront>;
}
