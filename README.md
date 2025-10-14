# capacitor-storefront

Capacitor Plugin to access information regarding store country

## Install

```bash
npm install capacitor-storefront
npx cap sync
```

## API

<docgen-index>

* [`initialize()`](#initialize)
* [`getStorefront()`](#getstorefront)
* [Interfaces](#interfaces)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### initialize()

```typescript
initialize() => Promise<void>
```

Initialize the underlying store client

--------------------


### getStorefront()

```typescript
getStorefront() => Promise<Storefront>
```

Gets the storefront for the current store account.

**Returns:** <code>Promise&lt;<a href="#storefront">Storefront</a>&gt;</code>

--------------------


### Interfaces


#### Storefront

| Prop              | Type                | Description                                |
| ----------------- | ------------------- | ------------------------------------------ |
| **`countryCode`** | <code>string</code> | Country code of the current store account. |

</docgen-api>
